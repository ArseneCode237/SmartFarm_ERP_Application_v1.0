package com.reseau_partage.auth.controller;

import com.reseau_partage.auth.dto.AuthResponse;
import com.reseau_partage.auth.dto.LoginRequest;
import com.reseau_partage.auth.dto.RegisterRequest;
import com.reseau_partage.auth.dto.RefreshTokenRequest;
import com.reseau_partage.auth.dto.ChangePasswordRequest;
import com.reseau_partage.auth.dto.ApiSuccess;
import com.reseau_partage.auth.dto.UpdateProfileRequest;
import com.reseau_partage.auth.dto.ProfileResponse;
import com.reseau_partage.auth.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.security.core.Authentication;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refresh(request.getRefreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiSuccess> logout(@RequestHeader("Authorization") String authorization) {
        authService.logout(authorization.substring(7));
        return ResponseEntity.ok(ApiSuccess.ok("Déconnexion réussie. Votre session a été fermée."));
    }

    @PostMapping("/logout-all")
    public ResponseEntity<ApiSuccess> logoutAll(Authentication authentication) {
        authService.logoutAll(authentication.getName());
        return ResponseEntity.ok(ApiSuccess.ok("Toutes vos sessions ont été fermées."));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiSuccess> changePassword(Authentication authentication, @Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(authentication.getName(), request.getCurrentPassword(), request.getNewPassword(), request.getConfirmNewPassword());
        return ResponseEntity.ok(ApiSuccess.ok("Mot de passe modifié avec succès. Veuillez vous reconnecter."));
    }

    @PatchMapping("/me")
    public ResponseEntity<ProfileResponse> updateProfile(Authentication authentication,
                                                           @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(authService.updateProfile(authentication.getName(), request));
    }

    @GetMapping("/test")
    public ResponseEntity<ApiSuccess> test() {
        return ResponseEntity.ok(ApiSuccess.ok("Accès autorisé : votre jeton est valide."));
    }
}
