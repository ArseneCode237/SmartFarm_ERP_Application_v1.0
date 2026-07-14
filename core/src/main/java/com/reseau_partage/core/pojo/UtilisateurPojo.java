package com.reseau_partage.core.pojo;

import java.sql.Date;
import java.util.List;

public class UtilisateurPojo {

    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private Long profil_id;
    private String fermeId;
    private String fermeNom;
    private List<String> typeActivite;
    private List<String> typeService;
    private String localisation;
    private String sexe;
    private Boolean actif;
    private Date dateCreation;
    private Date dateModification;

    public UtilisateurPojo() {
    }

    public UtilisateurPojo(Long id, String nom, String prenom, String email, String telephone,
                           Long profil_id, String fermeId, String fermeNom,
                           List<String> typeActivite, List<String> typeService, String localisation,
                           String sexe, Boolean actif, Date dateCreation, Date dateModification) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.profil_id = profil_id;
        this.fermeId = fermeId;
        this.fermeNom = fermeNom;
        this.typeActivite = typeActivite;
        this.typeService = typeService;
        this.localisation = localisation;
        this.sexe = sexe;
        this.actif = actif;
        this.dateCreation = dateCreation;
        this.dateModification = dateModification;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public List<String> getTypeActivite() { return typeActivite; }
    public void setTypeActivite(List<String> typeActivite) { this.typeActivite = typeActivite; }

    public List<String> getTypeService() { return typeService; }
    public void setTypeService(List<String> typeService) { this.typeService = typeService; }

    public String getLocalisation() { return localisation; }
    public void setLocalisation(String localisation) { this.localisation = localisation; }

    public Long getProfil_id() { return profil_id; }
    public void setProfil_id(Long profil_id) { this.profil_id = profil_id; }
    
    public String getFermeId() { return fermeId; }
    public void setFermeId(String fermeId) { this.fermeId = fermeId; }
    
    public String getFermeNom() { return fermeNom; }
    public void setFermeNom(String fermeNom) { this.fermeNom = fermeNom; }
    
    public String getSexe() { return sexe; }
    public void setSexe(String sexe) { this.sexe = sexe; }

    public Boolean getActif() { return actif; }
    public void setActif(Boolean actif) { this.actif = actif; }

    public Date getDateCreation() { return dateCreation; }
    public void setDateCreation(Date dateCreation) { this.dateCreation = dateCreation; }

    public Date getDateModification() { return dateModification; }
    public void setDateModification(Date dateModification) { this.dateModification = dateModification; }
}
