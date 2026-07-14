package com.reseau_partage.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RegisterRequest {
    private String nom;
    private String prenom;
    private String email;
    private String password;
    private String telephone;
    private String fermeId;
    private String fermeNom;
    @JsonProperty("type_activite")
    private String typeActivite;
    @JsonProperty("type_service")
    private String typeService;
    private String localisation;

    public RegisterRequest() {
    }

    public RegisterRequest(String nom, String prenom, String email, String password,
                           String telephone, String fermeId, String fermeNom,
                           String typeActivite, String typeService, String localisation) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.password = password;
        this.telephone = telephone;
        this.fermeId = fermeId;
        this.fermeNom = fermeNom;
        this.typeActivite = typeActivite;
        this.typeService = typeService;
        this.localisation = localisation;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getFermeId() {
        return fermeId;
    }

    public void setFermeId(String fermeId) {
        this.fermeId = fermeId;
    }

    public String getFermeNom() {
        return fermeNom;
    }

    public void setFermeNom(String fermeNom) {
        this.fermeNom = fermeNom;
    }

    public String getTypeActivite() {
        return typeActivite;
    }

    public void setTypeActivite(String typeActivite) {
        this.typeActivite = typeActivite;
    }

    public String getTypeService() {
        return typeService;
    }

    public void setTypeService(String typeService) {
        this.typeService = typeService;
    }

    public String getLocalisation() {
        return localisation;
    }

    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }

    public static RegisterRequestBuilder builder() {
        return new RegisterRequestBuilder();
    }

    public static class RegisterRequestBuilder {
        private String nom;
        private String prenom;
        private String email;
        private String password;
        private String telephone;
        private String fermeId;
        private String fermeNom;
        private String typeActivite;
        private String typeService;
        private String localisation;

        public RegisterRequestBuilder nom(String nom) {
            this.nom = nom;
            return this;
        }

        public RegisterRequestBuilder prenom(String prenom) {
            this.prenom = prenom;
            return this;
        }

        public RegisterRequestBuilder email(String email) {
            this.email = email;
            return this;
        }

        public RegisterRequestBuilder password(String password) {
            this.password = password;
            return this;
        }

        public RegisterRequestBuilder telephone(String telephone) {
            this.telephone = telephone;
            return this;
        }

        public RegisterRequestBuilder fermeId(String fermeId) {
            this.fermeId = fermeId;
            return this;
        }

        public RegisterRequestBuilder fermeNom(String fermeNom) {
            this.fermeNom = fermeNom;
            return this;
        }

        public RegisterRequestBuilder typeActivite(String typeActivite) {
            this.typeActivite = typeActivite;
            return this;
        }

        public RegisterRequestBuilder typeService(String typeService) {
            this.typeService = typeService;
            return this;
        }

        public RegisterRequestBuilder localisation(String localisation) {
            this.localisation = localisation;
            return this;
        }

        public RegisterRequest build() {
            return new RegisterRequest(nom, prenom, email, password, telephone, fermeId, fermeNom, typeActivite, typeService, localisation);
        }
    }
}
