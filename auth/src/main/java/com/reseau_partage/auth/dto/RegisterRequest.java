package com.reseau_partage.auth.dto;

public class RegisterRequest {

    private String nom;
    private String prenom;
    private String email;
    private String password;
    private String telephone;
    /** Adresse physique de l'utilisateur (rue, quartier, ville…). */
    private String adresse;
    private String sexe;

    public RegisterRequest() {}

    public RegisterRequest(String nom, String prenom, String email, String password,
                           String telephone, String adresse, String sexe) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.password = password;
        this.telephone = telephone;
        this.adresse = adresse;
        this.sexe = sexe;
    }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getSexe() { return sexe; }
    public void setSexe(String sexe) { this.sexe = sexe; }

    public static RegisterRequestBuilder builder() { return new RegisterRequestBuilder(); }

    public static class RegisterRequestBuilder {
        private String nom;
        private String prenom;
        private String email;
        private String password;
        private String telephone;
        private String adresse;
        private String sexe;

        public RegisterRequestBuilder nom(String nom) { this.nom = nom; return this; }
        public RegisterRequestBuilder prenom(String prenom) { this.prenom = prenom; return this; }
        public RegisterRequestBuilder email(String email) { this.email = email; return this; }
        public RegisterRequestBuilder password(String password) { this.password = password; return this; }
        public RegisterRequestBuilder telephone(String telephone) { this.telephone = telephone; return this; }
        public RegisterRequestBuilder adresse(String adresse) { this.adresse = adresse; return this; }
        public RegisterRequestBuilder sexe(String sexe) { this.sexe = sexe; return this; }

        public RegisterRequest build() {
            return new RegisterRequest(nom, prenom, email, password, telephone, adresse, sexe);
        }
    }
}
