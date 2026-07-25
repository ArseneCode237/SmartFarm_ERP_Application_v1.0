package com.reseau_partage.core.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "fermes", uniqueConstraints = @UniqueConstraint(columnNames = { "nom", "pays" }))
public class Ferme {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false, length = 100)
    private String pays;

    @Column(length = 10)
    private String devise;

    @Column(name = "fuseau_horaire", length = 50)
    private String fuseauHoraire;

    @Column(name = "superficie_totale", precision = 10, scale = 4)
    private BigDecimal superficieTotale;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "telephone_contact", length = 20)
    private String telephoneContact;

    @Column(name = "email_contact", length = 150)
    private String emailContact;

    /** Localisation géographique de la ferme (ville, région, etc.). */
    @Column(length = 500)
    private String localisation;

    /**
     * Types d'activité pratiqués sur la ferme.
     * Valeurs : agriculture, elevage, aviculture, pisciculture.
     */
    @ElementCollection
    @CollectionTable(name = "ferme_type_activites",
            joinColumns = @JoinColumn(name = "ferme_id"))
    @Column(name = "type_activite", nullable = false)
    @OrderColumn(name = "position")
    private List<String> typeActivite = new ArrayList<>();

    /**
     * Services activés sur la ferme.
     * Valeurs : stock, vaccination, comptabilite, maintenance, videosurveillance.
     */
    @ElementCollection
    @CollectionTable(name = "ferme_type_services",
            joinColumns = @JoinColumn(name = "ferme_id"))
    @Column(name = "type_service", nullable = false)
    @OrderColumn(name = "position")
    private List<String> typeService = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutFerme statut;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    /** ID de l'utilisateur propriétaire de la ferme. */
    @Column(name = "proprietaire_id", nullable = false, updatable = false)
    private Long proprietaireId;

    @PrePersist
    void prePersist() {
        if (dateCreation == null) dateCreation = LocalDateTime.now();
        if (statut == null) statut = StatutFerme.ACTIF;
    }

    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String v) {
        nom = v;
    }

    public String getPays() {
        return pays;
    }

    public void setPays(String v) {
        pays = v;
    }

    public String getDevise() {
        return devise;
    }

    public void setDevise(String v) {
        devise = v;
    }

    public String getFuseauHoraire() {
        return fuseauHoraire;
    }

    public void setFuseauHoraire(String v) {
        fuseauHoraire = v;
    }

    public BigDecimal getSuperficieTotale() {
        return superficieTotale;
    }

    public void setSuperficieTotale(BigDecimal v) {
        superficieTotale = v;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String v) {
        logoUrl = v;
    }

    public String getTelephoneContact() {
        return telephoneContact;
    }

    public void setTelephoneContact(String v) {
        telephoneContact = v;
    }

    public String getEmailContact() {
        return emailContact;
    }

    public void setEmailContact(String v) {
        emailContact = v;
    }

    public StatutFerme getStatut() { return statut; }
    public void setStatut(StatutFerme v) { statut = v; }

    public LocalDateTime getDateCreation() { return dateCreation; }

    public Long getProprietaireId() { return proprietaireId; }
    public void setProprietaireId(Long v) { proprietaireId = v; }

    public String getLocalisation() { return localisation; }
    public void setLocalisation(String v) { localisation = v; }

    public List<String> getTypeActivite() { return typeActivite; }
    public void setTypeActivite(List<String> v) {
        this.typeActivite = v == null ? new ArrayList<>() : new ArrayList<>(v);
    }

    public List<String> getTypeService() { return typeService; }
    public void setTypeService(List<String> v) {
        this.typeService = v == null ? new ArrayList<>() : new ArrayList<>(v);
    }
}
