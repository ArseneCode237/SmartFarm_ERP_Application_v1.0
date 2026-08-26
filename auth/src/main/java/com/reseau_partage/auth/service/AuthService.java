package com.reseau_partage.auth.service;

import com.reseau_partage.auth.dto.AuthResponse;
import com.reseau_partage.auth.dto.LoginRequest;
import com.reseau_partage.auth.dto.RegisterRequest;
import com.reseau_partage.auth.dto.UpdateProfileRequest;
import com.reseau_partage.auth.dto.ProfileResponse;
import com.reseau_partage.auth.exception.ConflictException;
import com.reseau_partage.auth.security.JwtUtils;
import com.reseau_partage.core.entities.Utilisateur;
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
        validateSexe(request.getSexe());

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(request.getNom());
        utilisateur.setPrenom(request.getPrenom());
        utilisateur.setEmail(request.getEmail());
        utilisateur.setTelephone(telephone);
        utilisateur.setSexe(request.getSexe());
        utilisateur.setAdresse(request.getAdresse());
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

    private String normalizeTelephone(String telephone) {
        if (telephone == null || telephone.isBlank()) {
            return null;
        }
        String normalized = telephone.trim().replaceAll("[\\s().-]", "");
        return normalized.startsWith("00") ? "+" + normalized.substring(2) : normalized;
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
        if (request.getAdresse() != null) {
            utilisateur.setAdresse(request.getAdresse().trim());
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
        String token = jwtUtils.generateToken(userDetails, Map.of("sid", session.getId()));
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
