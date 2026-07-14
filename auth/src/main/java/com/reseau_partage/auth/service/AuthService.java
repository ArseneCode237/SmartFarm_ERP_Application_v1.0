package com.reseau_partage.auth.service;

import com.reseau_partage.auth.dto.AuthResponse;
import com.reseau_partage.auth.dto.LoginRequest;
import com.reseau_partage.auth.dto.RegisterRequest;
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
            throw new RuntimeException("Email déjà utilisé");
        }
        validateTypeActivite(request.getTypeActivite());
        validateTypeService(request.getTypeService());

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(request.getNom());
        utilisateur.setPrenom(request.getPrenom());
        utilisateur.setEmail(request.getEmail());
        utilisateur.setTelephone(request.getTelephone());
        utilisateur.setStructureId(request.getFermeId());
        utilisateur.setStructureNom(request.getFermeNom());
        utilisateur.setTypeActivite(request.getTypeActivite());
        utilisateur.setTypeService(request.getTypeService());
        utilisateur.setLocalisation(request.getLocalisation());
        utilisateur.setMotDePasse(passwordEncoder.encode(request.getPassword()));
        utilisateur.setActif(true);
        utilisateur.setTentative_echec(0);
        utilisateur.setDateCreation(Date.valueOf(LocalDate.now()));

        // Assigner le profil USER par défaut
        profilRepository.findByCode("USER").ifPresent(profil -> utilisateur.setProfil_id(profil.getId()));

        utilisateurRepository.save(utilisateur);

        return createSessionResponse(utilisateur);
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

    private void validateTypeService(String typeService) {
        if (typeService == null) {
            return;
        }
        String normalized = typeService.trim().toLowerCase();
        if (!normalized.equals("stock") && !normalized.equals("vaccination") && !normalized.equals("comptabilite") && !normalized.equals("maintenance") && !normalized.equals("videosurveillance")) {
            throw new IllegalArgumentException("Type de service invalide. Valeurs acceptées : stock, vaccination, comptabilite, maintenance, videosurveillance.");
        }
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

        return createSessionResponse(utilisateur);
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
        return createSessionResponse(utilisateur);
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
    public void changePassword(String email, String currentPassword, String newPassword) {
        if (newPassword.length() < 8) {
            throw new IllegalArgumentException("Le nouveau mot de passe doit contenir au moins 8 caracteres");
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

    private AuthResponse createSessionResponse(Utilisateur utilisateur) {
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
        String token = jwtUtils.generateToken(userDetails, Map.of("sid", session.getId(), "structureId", String.valueOf(utilisateur.getStructureId())));
        return AuthResponse.builder().token(token).refreshToken(refreshToken)
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
