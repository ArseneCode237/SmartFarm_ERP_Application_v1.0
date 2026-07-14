package com.reseau_partage.core.entities;

import java.io.Serializable;
import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "utilisateur")
public class Utilisateur implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false, unique = true)
    private String email;

    private String telephone;

    @Column(nullable = false)
    private String motDePasse;

    private Long profil_id;

    private String structureId;

    private String structureNom;

    private String typeActivite;

    private String typeService;

    private String localisation;
    private String sexe;

    @Column(nullable = false)
    private Boolean actif;

    @Column(nullable = false)
    private int tentative_echec;

    private Date dateCreation;
    private Date bloque_jusqu_a;
    private Date dateModification;

    public Utilisateur() {
    }

    public Utilisateur(Long id, String nom, String prenom, String email, String telephone,
                       String motDePasse, Long profil_id, String structureId, String structureNom,
                       String typeActivite, String typeService, String localisation,
                       Boolean actif, int tentative_echec,
                       Date dateCreation, Date bloque_jusqu_a, Date dateModification) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.motDePasse = motDePasse;
        this.profil_id = profil_id;
        this.structureId = structureId;
        this.structureNom = structureNom;
        this.typeActivite = typeActivite;
        this.typeService = typeService;
        this.localisation = localisation;
        this.actif = actif;
        this.tentative_echec = tentative_echec;
        this.dateCreation = dateCreation;
        this.bloque_jusqu_a = bloque_jusqu_a;
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

    public String getTypeActivite() { return typeActivite; }
    public void setTypeActivite(String typeActivite) { this.typeActivite = typeActivite; }

    public String getTypeService() { return typeService; }
    public void setTypeService(String typeService) { this.typeService = typeService; }

    public String getLocalisation() { return localisation; }
    public void setLocalisation(String localisation) { this.localisation = localisation; }

    public String getSexe() { return sexe; }
    public void setSexe(String sexe) { this.sexe = sexe; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }

    public Long getProfil_id() { return profil_id; }
    public void setProfil_id(Long profil_id) { this.profil_id = profil_id; }

    public String getStructureId() { return structureId; }
    public void setStructureId(String structureId) { this.structureId = structureId; }

    public String getStructureNom() { return structureNom; }
    public void setStructureNom(String structureNom) { this.structureNom = structureNom; }

    public Boolean getActif() { return actif; }
    public void setActif(Boolean actif) { this.actif = actif; }

    public int getTentative_echec() { return tentative_echec; }
    public void setTentative_echec(int tentative_echec) { this.tentative_echec = tentative_echec; }

    public Date getDateCreation() { return dateCreation; }
    public void setDateCreation(Date dateCreation) { this.dateCreation = dateCreation; }

    public Date getBloque_jusqu_a() { return bloque_jusqu_a; }
    public void setBloque_jusqu_a(Date bloque_jusqu_a) { this.bloque_jusqu_a = bloque_jusqu_a; }

    public Date getDateModification() { return dateModification; }
    public void setDateModification(Date dateModification) { this.dateModification = dateModification; }

    @Override
    public String toString() {
        return "Utilisateur{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", telephone='" + telephone + '\'' +
                ", profil_id=" + profil_id +
                ", structureId=" + structureId +
                ", structureNom='" + structureNom + '\'' +
                ", typeActivite='" + typeActivite + '\'' +
                ", typeService='" + typeService + '\'' +
                ", localisation='" + localisation + '\'' +
                ", sexe='" + sexe + '\'' +
                ", actif=" + actif +
                ", tentative_echec=" + tentative_echec +
                ", dateCreation=" + dateCreation +
                ", bloque_jusqu_a=" + bloque_jusqu_a +
                ", dateModification=" + dateModification +
                '}';
    }
}
