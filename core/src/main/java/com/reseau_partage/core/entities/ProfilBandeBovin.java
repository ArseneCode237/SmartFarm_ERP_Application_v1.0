package com.reseau_partage.core.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

/**
 * Extension bovine d'une {@link Bande} (troupeau d'engraissement, allaitant ou laitier collectif).
 *
 * <p>Ne porte que le spécifique bovin : les indicateurs génériques de bande
 * (poids moyen actuel, FCR cumulé, GMQ, ration journalière, date de sortie prévue)
 * restent portés par {@link Bande} et ne sont volontairement pas dupliqués ici.</p>
 */
@Entity
@Table(name = "profils_bandes_bovins")
public class ProfilBandeBovin {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "bande_id")
    private Bande bande;

    // ── Type de production ─────────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(name = "type_production_bovin")
    private TypeProductionBovin typeProductionBovin;

    @Column(length = 100)
    private String race;

    // ── Objectifs d'engraissement ──────────────────────────────────────────
    @Column(name = "poids_cible_sortie_kg", precision = 7, scale = 1)
    private BigDecimal poidsCibleSortieKg;

    @Column(name = "age_cible_abattage_mois")
    private Integer ageCibleAbattageMois;

    @Column(name = "gmq_cible_g")
    private Integer gmqCibleG;

    @Column(name = "fcr_cible", precision = 5, scale = 2)
    private BigDecimal fcrCible;

    @Column(name = "duree_engraissement_jours")
    private Integer dureeEngraissementJours;

    // ── Aliment cumulé ─────────────────────────────────────────────────────
    @Column(name = "aliment_consomme_total_kg", precision = 12, scale = 2)
    private BigDecimal alimentConsommeTotalKg = BigDecimal.ZERO;

    // ── Abattage ───────────────────────────────────────────────────────────
    @Column(name = "date_abattage_prevue")
    private LocalDate dateAbattagePrevue;

    @Column(name = "date_abattage_reelle")
    private LocalDate dateAbattageReelle;

    @Column(name = "rendement_carcasse_pct", precision = 5, scale = 2)
    private BigDecimal rendementCarcassePct;

    // ── Audit ──────────────────────────────────────────────────────────────
    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        dateModification = dateCreation;
    }

    @PreUpdate
    protected void onUpdate() {
        dateModification = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Bande getBande() { return bande; }
    public void setBande(Bande bande) { this.bande = bande; }
    public TypeProductionBovin getTypeProductionBovin() { return typeProductionBovin; }
    public void setTypeProductionBovin(TypeProductionBovin value) { typeProductionBovin = value; }
    public String getRace() { return race; }
    public void setRace(String race) { this.race = race; }
    public BigDecimal getPoidsCibleSortieKg() { return poidsCibleSortieKg; }
    public void setPoidsCibleSortieKg(BigDecimal value) { poidsCibleSortieKg = value; }
    public Integer getAgeCibleAbattageMois() { return ageCibleAbattageMois; }
    public void setAgeCibleAbattageMois(Integer value) { ageCibleAbattageMois = value; }
    public Integer getGmqCibleG() { return gmqCibleG; }
    public void setGmqCibleG(Integer value) { gmqCibleG = value; }
    public BigDecimal getFcrCible() { return fcrCible; }
    public void setFcrCible(BigDecimal value) { fcrCible = value; }
    public Integer getDureeEngraissementJours() { return dureeEngraissementJours; }
    public void setDureeEngraissementJours(Integer value) { dureeEngraissementJours = value; }
    public BigDecimal getAlimentConsommeTotalKg() { return alimentConsommeTotalKg; }
    public void setAlimentConsommeTotalKg(BigDecimal value) { alimentConsommeTotalKg = value; }
    public LocalDate getDateAbattagePrevue() { return dateAbattagePrevue; }
    public void setDateAbattagePrevue(LocalDate value) { dateAbattagePrevue = value; }
    public LocalDate getDateAbattageReelle() { return dateAbattageReelle; }
    public void setDateAbattageReelle(LocalDate value) { dateAbattageReelle = value; }
    public BigDecimal getRendementCarcassePct() { return rendementCarcassePct; }
    public void setRendementCarcassePct(BigDecimal value) { rendementCarcassePct = value; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public LocalDateTime getDateModification() { return dateModification; }
}
