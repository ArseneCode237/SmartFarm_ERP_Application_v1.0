package com.reseau_partage.core.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "sites", uniqueConstraints = @UniqueConstraint(columnNames = { "ferme_id", "nom" }))
public class Site {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ferme_id", nullable = false)
    private Ferme ferme;
    @Column(nullable = false)
    private String nom;
    private String adresse;
    private String ville;
    private String region;
    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;
    @Column(precision = 10, scale = 7)
    private BigDecimal longitude;
    @Column(precision = 10, scale = 4)
    private BigDecimal superficie;
    @Column(name = "responsable_nom", length = 150)
    private String responsableNom;
    @Column(name = "responsable_telephone", length = 20)
    private String responsableTelephone;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutSite statut;
    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @PrePersist
    void prePersist() {
        if (dateCreation == null)
            dateCreation = LocalDateTime.now();
        if (statut == null)
            statut = StatutSite.ACTIF;
    }

    public Long getId() {
        return id;
    }

    public Ferme getFerme() {
        return ferme;
    }

    public void setFerme(Ferme v) {
        ferme = v;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String v) {
        nom = v;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String v) {
        adresse = v;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String v) {
        ville = v;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String v) {
        region = v;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal v) {
        latitude = v;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal v) {
        longitude = v;
    }

    public BigDecimal getSuperficie() {
        return superficie;
    }

    public void setSuperficie(BigDecimal v) {
        superficie = v;
    }

    public String getResponsableNom() {
        return responsableNom;
    }

    public void setResponsableNom(String v) {
        responsableNom = v;
    }

    public String getResponsableTelephone() {
        return responsableTelephone;
    }

    public void setResponsableTelephone(String v) {
        responsableTelephone = v;
    }

    public StatutSite getStatut() {
        return statut;
    }

    public void setStatut(StatutSite v) {
        statut = v;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }
}
