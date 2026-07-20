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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

/**
 * Profil reproductif détaillé d'un animal porcin (truie ou verrat).
 * Lié en OneToOne à Animal — la même clé primaire est partagée (@MapsId).
 * Ne pas créer directement : passe par PorcinService ou ExtractionBandeService.
 */
@Entity
@Table(name = "profils_porcins")
public class ProfilPorcin {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "animal_id")
    private Animal animal;

    // ── Statut reproductif détaillé ───────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(name = "statut_reproductif", nullable = false)
    private StatutReproductifPorcin statutReproductif = StatutReproductifPorcin.COCHETTE;

    @Column(name = "date_debut_statut_actuel")
    private LocalDate dateDebutStatutActuel;

    // ── Saillie active ────────────────────────────────────────────────────────
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "saillie_active_id")
    private Saillie saillieActive;

    // ── Carrière reproductive ─────────────────────────────────────────────────
    @Column(name = "numero_portee_actuelle")
    private Integer numeroPorteeActuelle = 0;

    @Column(name = "nb_portees_total")
    private Integer nbPorteesTotal = 0;

    @Column(name = "nb_porcelets_total_nes_vivants")
    private Integer nbPorceletsTotalNesVivants = 0;

    @Column(name = "nb_porcelets_total_mort_nes")
    private Integer nbPorceletsTotalMortNes = 0;

    @Column(name = "nb_porcelets_total_sevres")
    private Integer nbPorceletsTotalSevres = 0;

    // ── Moyennes calculées ────────────────────────────────────────────────────
    @Column(name = "moy_nes_vivants_par_portee", precision = 5, scale = 2)
    private BigDecimal moyNesVivantsParPortee;

    @Column(name = "moy_poids_sevrage_kg", precision = 6, scale = 3)
    private BigDecimal moyPoidsSevrageKg;

    @Column(name = "moy_duree_lactation_jours", precision = 5, scale = 1)
    private BigDecimal moyDureeLactationJours;

    @Column(name = "moy_retour_chaleur_jours", precision = 5, scale = 1)
    private BigDecimal moyRetourChaleurJours;

    // ── Dates planifiées (calculées automatiquement) ──────────────────────────
    @Column(name = "date_mise_bas_prevue")
    private LocalDate dateMiseBasPrevue;

    @Column(name = "date_sevrage_prevu")
    private LocalDate dateSevragePrevu;

    @Column(name = "date_retour_chaleur_estimee")
    private LocalDate dateRetourChaleurEstimee;

    @Column(name = "date_prochaine_saillie_prevue")
    private LocalDate dateProchainesSailliePrevue;

    // ── Origine / Extraction ──────────────────────────────────────────────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bande_origine_id")
    private Bande bandeOrigine;

    @Column(name = "date_extraction_bande")
    private LocalDate dateExtractionBande;

    @Column(name = "poids_selection_kg", precision = 8, scale = 3)
    private BigDecimal poidsSelectionKg;

    // ── Audit ─────────────────────────────────────────────────────────────────
    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @PrePersist
    protected void onCreate() {
        this.dateCreation    = LocalDateTime.now();
        this.dateModification = LocalDateTime.now();
        if (this.dateDebutStatutActuel == null) {
            this.dateDebutStatutActuel = LocalDate.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.dateModification = LocalDateTime.now();
    }

    // ── Getters / Setters ─────────────────────────────────────────────────────

    public Long getId() { return id; }

    public Animal getAnimal() { return animal; }
    public void setAnimal(Animal animal) { this.animal = animal; }

    public StatutReproductifPorcin getStatutReproductif() { return statutReproductif; }
    public void setStatutReproductif(StatutReproductifPorcin v) {
        this.statutReproductif = v;
        this.dateDebutStatutActuel = LocalDate.now();
    }

    public LocalDate getDateDebutStatutActuel() { return dateDebutStatutActuel; }
    public void setDateDebutStatutActuel(LocalDate v) { this.dateDebutStatutActuel = v; }

    public Saillie getSaillieActive() { return saillieActive; }
    public void setSaillieActive(Saillie saillieActive) { this.saillieActive = saillieActive; }

    public Integer getNumeroPorteeActuelle() { return numeroPorteeActuelle; }
    public void setNumeroPorteeActuelle(Integer v) { this.numeroPorteeActuelle = v; }

    public Integer getNbPorteesTotal() { return nbPorteesTotal; }
    public void setNbPorteesTotal(Integer v) { this.nbPorteesTotal = v; }

    public Integer getNbPorceletsTotalNesVivants() { return nbPorceletsTotalNesVivants; }
    public void setNbPorceletsTotalNesVivants(Integer v) { this.nbPorceletsTotalNesVivants = v; }

    public Integer getNbPorceletsTotalMortNes() { return nbPorceletsTotalMortNes; }
    public void setNbPorceletsTotalMortNes(Integer v) { this.nbPorceletsTotalMortNes = v; }

    public Integer getNbPorceletsTotalSevres() { return nbPorceletsTotalSevres; }
    public void setNbPorceletsTotalSevres(Integer v) { this.nbPorceletsTotalSevres = v; }

    public BigDecimal getMoyNesVivantsParPortee() { return moyNesVivantsParPortee; }
    public void setMoyNesVivantsParPortee(BigDecimal v) { this.moyNesVivantsParPortee = v; }

    public BigDecimal getMoyPoidsSevrageKg() { return moyPoidsSevrageKg; }
    public void setMoyPoidsSevrageKg(BigDecimal v) { this.moyPoidsSevrageKg = v; }

    public BigDecimal getMoyDureeLactationJours() { return moyDureeLactationJours; }
    public void setMoyDureeLactationJours(BigDecimal v) { this.moyDureeLactationJours = v; }

    public BigDecimal getMoyRetourChaleurJours() { return moyRetourChaleurJours; }
    public void setMoyRetourChaleurJours(BigDecimal v) { this.moyRetourChaleurJours = v; }

    public LocalDate getDateMiseBasPrevue() { return dateMiseBasPrevue; }
    public void setDateMiseBasPrevue(LocalDate dateMiseBasPrevue) { this.dateMiseBasPrevue = dateMiseBasPrevue; }

    public LocalDate getDateSevragePrevu() { return dateSevragePrevu; }
    public void setDateSevragePrevu(LocalDate dateSevragePrevu) { this.dateSevragePrevu = dateSevragePrevu; }

    public LocalDate getDateRetourChaleurEstimee() { return dateRetourChaleurEstimee; }
    public void setDateRetourChaleurEstimee(LocalDate v) { this.dateRetourChaleurEstimee = v; }

    public LocalDate getDateProchainesSailliePrevue() { return dateProchainesSailliePrevue; }
    public void setDateProchainesSailliePrevue(LocalDate v) { this.dateProchainesSailliePrevue = v; }

    public Bande getBandeOrigine() { return bandeOrigine; }
    public void setBandeOrigine(Bande bandeOrigine) { this.bandeOrigine = bandeOrigine; }

    public LocalDate getDateExtractionBande() { return dateExtractionBande; }
    public void setDateExtractionBande(LocalDate dateExtractionBande) { this.dateExtractionBande = dateExtractionBande; }

    public BigDecimal getPoidsSelectionKg() { return poidsSelectionKg; }
    public void setPoidsSelectionKg(BigDecimal poidsSelectionKg) { this.poidsSelectionKg = poidsSelectionKg; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public LocalDateTime getDateModification() { return dateModification; }
}
