package com.reseau_partage.auth.service;

import com.reseau_partage.auth.dto.AuthResponse;
import com.reseau_partage.auth.dto.LoginRequest;
import com.reseau_partage.auth.dto.RegisterRequest;
import com.reseau_partage.auth.dto.UpdateProfileRequest;
import com.reseau_partage.auth.dto.ProfileResponse;
import com.reseau_partage.auth.exception.ConflictException;
import com.reseau_partage.auth.security.JwtUtils;
import com.reseau_partage.core.entities.Utilisateur;
import com.reseau_partage.core.entities.Profil;
import com.reseau_partage.core.mappers.UtilisateurMapper;
import com.reseau_partage.core.repository.UtilisateurRepository;
import com.reseau_partage.core.repository.ProfilRepository;
import com.reseau_partage.core.repository.SessionRepository;
import com.reseau_partage.core.entities.Session;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.Instant;
import java.util.Map;
import java.util.List;
import java.util.LinkedHashSet;
import java.util.Set;
import java.text.Normalizer;
import java.util.UUID;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final ProfilRepository profilRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final SessionRepository sessionRepository;

    public AuthService(UtilisateurRepository utilisateurRepository,
                       ProfilRepository profilRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtils jwtUtils,
                       AuthenticationManager authenticationManager,
                       UserDetailsService userDetailsService,
                       SessionRepository sessionRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.profilRepository = profilRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.sessionRepository = sessionRepository;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (utilisateurRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email deja utilise");
        }
        String telephone = normalizeTelephone(request.getTelephone());
        if (telephone != null && utilisateurRepository.existsByTelephone(telephone)) {
            throw new ConflictException("Numero de telephone deja utilise");
        }
        String fermeNom = normalizeFermeNom(request.getFermeNom());
        if (fermeNom != null && utilisateurRepository.existsByStructureNomIgnoreCase(fermeNom)) {
            throw new ConflictException("Nom de ferme deja utilise");
        }
        List<String> typeActivites = normalizeAndValidateChoices(
                request.getTypeActivite(),
                Set.of("agriculture", "elevage", "aviculture", "pisciculture"),
                "activite"
        );
        List<String> typeServices = normalizeAndValidateChoices(
                request.getTypeService(),
                Set.of("stock", "vaccination", "comptabilite", "maintenance", "videosurveillance"),
                "service"
        );
        validateSexe(request.getSexe());

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(request.getNom());
        utilisateur.setPrenom(request.getPrenom());
        utilisateur.setEmail(request.getEmail());
        utilisateur.setTelephone(telephone);
        utilisateur.setStructureId(request.getFermeId());
        utilisateur.setStructureNom(fermeNom);
        utilisateur.setTypeActivite(typeActivites);
        utilisateur.setTypeService(typeServices);
        utilisateur.setSexe(request.getSexe());
        utilisateur.setLocalisation(request.getLocalisation());
        utilisateur.setMotDePasse(passwordEncoder.encode(request.getPassword()));
        utilisateur.setActif(true);
        utilisateur.setTentative_echec(0);
        utilisateur.setDateCreation(Date.valueOf(LocalDate.now()));

        // Assigner le profil USER par défaut
        profilRepository.findByCode("USER").ifPresent(profil -> utilisateur.setProfil_id(profil.getId()));

        utilisateurRepository.save(utilisateur);

        return createSessionResponse(utilisateur, "Compte cree avec succes.");
    }

    private void validateSexe(String sexe) {
        if (sexe == null) return;
        String normalized = sexe.trim().toLowerCase();
        if (!normalized.equals("masculin") && !normalized.equals("feminin")) {
            throw new IllegalArgumentException("Sexe invalide. Valeurs acceptées : Masculin, Feminin.");
        }
    }

    private void validateTypeActivite(String typeActivite) {
        if (typeActivite == null) {
            return;
        }
        String normalized = typeActivite.trim().toLowerCase();
        if (!normalized.equals("agriculture") && !normalized.equals("elevage") && !normalized.equals("aviculture") && !normalized.equals("pisciculture")) {
            throw new IllegalArgumentException("Type d'activite invalide. Valeurs acceptées : agriculture, elevage, aviculture, pisciculture.");
        }
    }

    private String normalizeTelephone(String telephone) {
        if (telephone == null || telephone.isBlank()) {
            return null;
        }
        String normalized = telephone.trim().replaceAll("[\\s().-]", "");
        return normalized.startsWith("00") ? "+" + normalized.substring(2) : normalized;
    }

    private String normalizeFermeNom(String fermeNom) {
        if (fermeNom == null || fermeNom.isBlank()) {
            return null;
        }
        return fermeNom.trim();
    }

    private void validateTypeService(String typeService) {
        if (typeService == null) {
            return;
        }
        String normalized = typeService.trim().toLowerCase();
        if (!normalized.equals("stock") && !normalized.equals("vaccination") && !normalized.equals("comptabilite") && !normalized.equals("maintenance") && !normalized.equals("videosurveillance")) {
            throw new IllegalArgumentException("Type de service invalide. Valeurs acceptées : stock, vaccination, comptabilite, maintenance, videosurveillance.");
        }
    }

    private List<String> normalizeAndValidateChoices(List<String> choices, Set<String> allowedChoices, String label) {
        if (choices == null || choices.isEmpty()) {
            return List.of();
        }

        LinkedHashSet<String> normalizedChoices = new LinkedHashSet<>();
        for (String choice : choices) {
            if (choice == null || choice.isBlank()) {
                throw new IllegalArgumentException("Chaque " + label + " selectionne doit etre renseigne.");
            }

            String normalized = Normalizer.normalize(choice.trim().toLowerCase(), Normalizer.Form.NFD)
                    .replaceAll("\\p{M}", "");
            if (!allowedChoices.contains(normalized)) {
                throw new IllegalArgumentException("Type d'" + label + " invalide : " + choice
                        + ". Valeurs acceptees : " + String.join(", ", allowedChoices) + ".");
            }
            normalizedChoices.add(normalized);
        }
        return List.copyOf(normalizedChoices);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        Utilisateur utilisateur = utilisateurRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec l'email: " + request.getEmail()));

        return createSessionResponse(utilisateur, "Connexion reussie.");
    }

    @Transactional
    public AuthResponse refresh(String refreshToken) {
        if (!jwtUtils.isRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("Le jeton fourni n'est pas un refresh token");
        }
        Long sessionId = jwtUtils.extractSessionId(refreshToken);
        Session session = sessionId == null ? null : sessionRepository.findByIdAndActiveTrue(sessionId).orElse(null);
        if (session == null || session.getDateExpiration().before(new java.util.Date()) || !hash(refreshToken).equals(session.getTokenHash())) {
            throw new IllegalArgumentException("Session invalide ou expiree");
        }
        session.setActive(false); // rotation: a refresh token cannot be reused
        sessionRepository.save(session);
        Utilisateur utilisateur = utilisateurRepository.findByEmail(jwtUtils.extractUsername(refreshToken))
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouve"));
        return createSessionResponse(utilisateur, "Session renouvelee avec succes.");
    }

    @Transactional
    public void logout(String token) {
        Long sessionId = jwtUtils.extractSessionId(token);
        if (sessionId != null) {
            sessionRepository.findById(sessionId).ifPresent(session -> {
                session.setActive(false);
                sessionRepository.save(session);
            });
        }
    }

    @Transactional
    public void logoutAll(String email) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouve"));
        sessionRepository.findAllByUtilisateurId(utilisateur.getId()).forEach(session -> session.setActive(false));
    }

    @Transactional
    public void changePassword(String email, String currentPassword, String newPassword, String confirmNewPassword) {
        if (newPassword.length() < 8) {
            throw new IllegalArgumentException("Le nouveau mot de passe doit contenir au moins 8 caracteres");
        }
        if (!newPassword.equals(confirmNewPassword)) {
            throw new IllegalArgumentException("La confirmation du nouveau mot de passe ne correspond pas.");
        }
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouve"));
        if (!passwordEncoder.matches(currentPassword, utilisateur.getMotDePasse())) {
            throw new IllegalArgumentException("Mot de passe actuel incorrect");
        }
        utilisateur.setMotDePasse(passwordEncoder.encode(newPassword));
        utilisateur.setDateModification(Date.valueOf(LocalDate.now()));
        utilisateurRepository.save(utilisateur);
        logoutAll(email);
    }

    @Transactional
    public ProfileResponse updateProfile(String currentEmail, UpdateProfileRequest request) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouve"));

        boolean emailChanged = false;
        if (request.getNom() != null) {
            utilisateur.setNom(requiredText(request.getNom(), "Le nom"));
        }
        if (request.getPrenom() != null) {
            utilisateur.setPrenom(requiredText(request.getPrenom(), "Le prenom"));
        }
        if (request.getEmail() != null) {
            String newEmail = requiredText(request.getEmail(), "L'adresse email").toLowerCase();
            emailChanged = !newEmail.equalsIgnoreCase(utilisateur.getEmail());
            if (emailChanged && utilisateurRepository.existsByEmail(newEmail)) {
                throw new ConflictException("Cette adresse email est deja utilisee.");
            }
            utilisateur.setEmail(newEmail);
        }
        if (request.getTelephone() != null) {
            String telephone = normalizeTelephone(request.getTelephone());
            if (telephone != null && !telephone.equals(utilisateur.getTelephone())
                    && utilisateurRepository.existsByTelephone(telephone)) {
                throw new ConflictException("Ce numero de telephone est deja utilise.");
            }
            utilisateur.setTelephone(telephone);
        }
        if (request.getFermeId() != null) {
            utilisateur.setStructureId(request.getFermeId().trim());
        }
        if (request.getFermeNom() != null) {
            String fermeNom = normalizeFermeNom(request.getFermeNom());
            if (fermeNom != null && !fermeNom.equalsIgnoreCase(utilisateur.getStructureNom())
                    && utilisateurRepository.existsByStructureNomIgnoreCase(fermeNom)) {
                throw new ConflictException("Ce nom de ferme est deja utilise.");
            }
            utilisateur.setStructureNom(fermeNom);
        }
        if (request.getTypeActivite() != null) {
            utilisateur.setTypeActivite(normalizeAndValidateChoices(request.getTypeActivite(),
                    Set.of("agriculture", "elevage", "aviculture", "pisciculture"), "activite"));
        }
        if (request.getTypeService() != null) {
            utilisateur.setTypeService(normalizeAndValidateChoices(request.getTypeService(),
                    Set.of("stock", "vaccination", "comptabilite", "maintenance", "videosurveillance"), "service"));
        }
        if (request.getLocalisation() != null) {
            utilisateur.setLocalisation(request.getLocalisation().trim());
        }
        if (request.getSexe() != null) {
            validateSexe(request.getSexe());
            utilisateur.setSexe(request.getSexe().trim().toLowerCase());
        }

        utilisateur.setDateModification(Date.valueOf(LocalDate.now()));
        utilisateurRepository.save(utilisateur);

        if (emailChanged) {
            logoutAll(utilisateur.getEmail());
            return new ProfileResponse(true, 200,
                    "Profil modifie. Votre adresse email a change : veuillez vous reconnecter.", true,
                    UtilisateurMapper.toPojo(utilisateur));
        }
        return new ProfileResponse(true, 200, "Profil modifie avec succes.", false,
                UtilisateurMapper.toPojo(utilisateur));
    }

    private String requiredText(String value, String fieldLabel) {
        if (value.isBlank()) {
            throw new IllegalArgumentException(fieldLabel + " est obligatoire.");
        }
        return value.trim();
    }

    private AuthResponse createSessionResponse(Utilisateur utilisateur, String message) {
        Session session = new Session();
        session.setUtilisateurId(utilisateur.getId());
        session.setTokenHash(UUID.randomUUID().toString());
        session.setDateCreation(java.util.Date.from(Instant.now()));
        session.setDateExpiration(java.util.Date.from(Instant.now().plusMillis(2_592_000_000L)));
        session.setActive(true);
        session = sessionRepository.save(session);

        UserDetails userDetails = userDetailsService.loadUserByUsername(utilisateur.getEmail());
        String refreshToken = jwtUtils.generateRefreshToken(userDetails, session.getId());
        session.setTokenHash(hash(refreshToken));
        sessionRepository.save(session);
        String token = jwtUtils.generateToken(userDetails, Map.of("sid", session.getId(), "fermeId", String.valueOf(utilisateur.getStructureId())));
        return AuthResponse.builder().message(message).token(token).refreshToken(refreshToken)
                .email(utilisateur.getEmail())
                .user(UtilisateurMapper.toPojo(utilisateur))
                .build();
    }

    private String hash(String value) {
        try {
            return java.util.HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (java.security.NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 indisponible", exception);
        }
    }
}
