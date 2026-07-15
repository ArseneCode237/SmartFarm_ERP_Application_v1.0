package com.reseau_partage.auth.dto;

import jakarta.validation.constraints.Email;

/** Champs modifiables du profil utilisateur. Les champs absents ne sont pas modifiés. */
public class UpdateProfileRequest {

    private String nom;
    private String prenom;

    @Email(message = "L'adresse email est invalide.")
    private String email;

    private String telephone;
    private String adresse;
    private String sexe;

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getSexe() { return sexe; }
    public void setSexe(String sexe) { this.sexe = sexe; }
}
