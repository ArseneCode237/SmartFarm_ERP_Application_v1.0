package com.reseau_partage.auth.dto;

import com.reseau_partage.core.pojo.UtilisateurPojo;

public class AuthResponse {
    private final boolean success = true;
    private final int status = 200;
    private String message;
    private String token;
    private String refreshToken;
    private String email;
    private UtilisateurPojo user;

    public AuthResponse() {
    }

    public AuthResponse(String message, String token, String refreshToken, String email, UtilisateurPojo user) {
        this.message = message;
        this.token = token;
        this.refreshToken = refreshToken;
        this.email = email;
        this.user = user;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isSuccess() { return success; }
    public int getStatus() { return status; }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UtilisateurPojo getUser() {
        return user;
    }

    public void setUser(UtilisateurPojo user) {
        this.user = user;
    }

    public static AuthResponseBuilder builder() {
        return new AuthResponseBuilder();
    }

    public static class AuthResponseBuilder {
        private String message;
        private String token;
        private String refreshToken;
        private String email;
        private UtilisateurPojo user;

        public AuthResponseBuilder token(String token) {
            this.token = token;
            return this;
        }

        public AuthResponseBuilder refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }

        public AuthResponseBuilder email(String email) {
            this.email = email;
            return this;
        }

        public AuthResponseBuilder user(UtilisateurPojo user) {
            this.user = user;
            return this;
        }

        public AuthResponseBuilder message(String message) {
            this.message = message;
            return this;
        }

        public AuthResponse build() {
            return new AuthResponse(message, token, refreshToken, email, user);
        }
    }
}
