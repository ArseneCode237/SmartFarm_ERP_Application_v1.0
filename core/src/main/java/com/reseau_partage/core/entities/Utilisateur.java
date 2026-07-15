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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(unique = true)
    private String telephone;

    @Column(nullable = false)
    private String motDePasse;

    private Long profil_id;

    /** Adresse physique de l'utilisateur (rue, quartier, ville…). */
    private String adresse;

    private String sexe;

    @Column(nullable = false)
    private Boolean actif;

    @Column(nullable = false)
    private int tentative_echec;

    private Date dateCreation;
    private Date bloque_jusqu_a;
    private Date dateModification;

    public Utilisateur() {}

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

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }

    public Long getProfil_id() { return profil_id; }
    public void setProfil_id(Long profil_id) { this.profil_id = profil_id; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getSexe() { return sexe; }
    public void setSexe(String sexe) { this.sexe = sexe; }

    public Boolean getActif() { return actif; }
    public void setActif(Boolean actif) { this.actif = actif; }

    public int getTentative_echec() { return tentative_echec; }
    public void setTentative_echec(int v) { this.tentative_echec = v; }

    public Date getDateCreation() { return dateCreation; }
    public void setDateCreation(Date dateCreation) { this.dateCreation = dateCreation; }

    public Date getBloque_jusqu_a() { return bloque_jusqu_a; }
    public void setBloque_jusqu_a(Date v) { this.bloque_jusqu_a = v; }

    public Date getDateModification() { return dateModification; }
    public void setDateModification(Date dateModification) { this.dateModification = dateModification; }

    @Override
    public String toString() {
        return "Utilisateur{id=" + id + ", nom='" + nom + "', email='" + email + "', actif=" + actif + "}";
    }
}
