package com.reseau_partage.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.Email;

import java.util.List;

/** Champs modifiables du profil. Les champs absents ne sont pas modifies. */
public class UpdateProfileRequest {
    private String nom;
    private String prenom;
    @Email(message = "L'adresse email est invalide.")
    private String email;
    private String telephone;
    private String fermeId;
    private String fermeNom;
    @JsonProperty("type_activite")
    @JsonDeserialize(using = CommaSeparatedListDeserializer.class)
    private List<String> typeActivite;
    @JsonProperty("type_service")
    @JsonDeserialize(using = CommaSeparatedListDeserializer.class)
    private List<String> typeService;
    private String localisation;
    private String sexe;

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String getFermeId() { return fermeId; }
    public void setFermeId(String fermeId) { this.fermeId = fermeId; }
    public String getFermeNom() { return fermeNom; }
    public void setFermeNom(String fermeNom) { this.fermeNom = fermeNom; }
    public List<String> getTypeActivite() { return typeActivite; }
    public void setTypeActivite(List<String> typeActivite) { this.typeActivite = typeActivite; }
    public List<String> getTypeService() { return typeService; }
    public void setTypeService(List<String> typeService) { this.typeService = typeService; }
    public String getLocalisation() { return localisation; }
    public void setLocalisation(String localisation) { this.localisation = localisation; }
    public String getSexe() { return sexe; }
    public void setSexe(String sexe) { this.sexe = sexe; }
}
