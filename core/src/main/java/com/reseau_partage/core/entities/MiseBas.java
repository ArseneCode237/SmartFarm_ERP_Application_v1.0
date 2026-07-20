package com.reseau_partage.core.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

/**
 * Résultat d'une gestation confirmée.
 * Une MiseBas est toujours liée à une Saillie de statut CONFIRMEE.
 */
@Entity
@Table(name = "mises_bas")
public class MiseBas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "saillie_id", nullable = false, unique = true)
    private Saillie saillie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "truie_id", nullable = false)
    private Animal truie;

    /** Numéro de portée de cette truie (1ère, 2ème…). */
    @Column(name = "numero_portee", nullable = false)
    private Integer numeroPortee;

    /** Libellé convivial de la portée (ex. "Portée #3 de Truie X"). */
    @Column(name = "nom", length = 150)
    private String nom;

    // ── Déroulement ───────────────────────────────────────────────────────────
    @Column(name = "date_mise_bas_reelle", nullable = false)
    private LocalDateTime dateMiseBasReelle;

    @Column(name = "duree_mise_bas_minutes")
    private Integer dureeMiseBaMinutes;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_mise_bas")
    private TypeMiseBas typeMiseBas;

    // ── Résultats ─────────────────────────────────────────────────────────────
    @Column(name = "nb_nes_vivants", nullable = false)
    private Integer nbNesVivants;

    @Column(name = "nb_mort_nes")
    private Integer nbMortNes = 0;

    @Column(name = "nb_momifies")
    private Integer nbMomifies = 0;

    @Column(name = "poids_moyen_naissance_kg", precision = 5, scale = 3)
    private BigDecimal poidsMoyenNaissanceKg;

    @Column(name = "poids_min_naissance_kg", precision = 5, scale = 3)
    private BigDecimal poidsMinNaissanceKg;

    @Column(name = "poids_max_naissance_kg", precision = 5, scale = 3)
    private BigDecimal poidsMaxNaissanceKg;

    // ── Allaitement ───────────────────────────────────────────────────────────
    /** Peut différer de nbNesVivants (adoptions, surpopulation). */
    @Column(name = "nb_porcelets_allaites")
    private Integer nbPorceletsAllaites;

    // ── Sevrage ───────────────────────────────────────────────────────────────
    /** dateMiseBas + durée sevrage configurée (généralement 21 jours). */
    @Column(name = "date_sevrage_prevu")
    private LocalDate dateSevragePrevu;

    @Column(name = "date_sevrage_reel")
    private LocalDate dateSevrageReel;

    @Column(name = "nb_sevres")
    private Integer nbSevres;

    @Column(name = "poids_moyen_sevrage_kg", precision = 6, scale = 3)
    private BigDecimal poidsMoyenSevrageKg;

    /** Calculé automatiquement à la date du sevrage. */
    @Column(name = "duree_lactation_jours")
    private Integer dureeLactationJours;

    // ── Destination des porcelets ─────────────────────────────────────────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bande_destination_id")
    private Bande bandeDestination;

    /** Nombre de jours depuis la portée précédente (calculé auto). */
    @Column(name = "jours_depuis_portee_precedente")
    private Integer joursdepuisPorteePrecedente;

    // ── Opérateur ─────────────────────────────────────────────────────────────
    @Column(name = "veterinaire_nom", length = 100)
    private String veterinaireNom;

    @Column(name = "operateur_nom", length = 100)
    private String operateurNom;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    @PrePersist
    protected void onCreate() {
        this.dateCreation = LocalDateTime.now();
    }

    // ── Getters / Setters ─────────────────────────────────────────────────────

    public Long getId() { return id; }

    public Saillie getSaillie() { return saillie; }
    public void setSaillie(Saillie saillie) { this.saillie = saillie; }

    public Animal getTruie() { return truie; }
    public void setTruie(Animal truie) { this.truie = truie; }

    public Integer getNumeroPortee() { return numeroPortee; }
    public void setNumeroPortee(Integer numeroPortee) { this.numeroPortee = numeroPortee; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public LocalDateTime getDateMiseBasReelle() { return dateMiseBasReelle; }
    public void setDateMiseBasReelle(LocalDateTime dateMiseBasReelle) { this.dateMiseBasReelle = dateMiseBasReelle; }

    public Integer getDureeMiseBaMinutes() { return dureeMiseBaMinutes; }
    public void setDureeMiseBaMinutes(Integer v) { this.dureeMiseBaMinutes = v; }

    public TypeMiseBas getTypeMiseBas() { return typeMiseBas; }
    public void setTypeMiseBas(TypeMiseBas typeMiseBas) { this.typeMiseBas = typeMiseBas; }

    public Integer getNbNesVivants() { return nbNesVivants; }
    public void setNbNesVivants(Integer nbNesVivants) { this.nbNesVivants = nbNesVivants; }

    public Integer getNbMortNes() { return nbMortNes; }
    public void setNbMortNes(Integer nbMortNes) { this.nbMortNes = nbMortNes; }

    public Integer getNbMomifies() { return nbMomifies; }
    public void setNbMomifies(Integer nbMomifies) { this.nbMomifies = nbMomifies; }

    public BigDecimal getPoidsMoyenNaissanceKg() { return poidsMoyenNaissanceKg; }
    public void setPoidsMoyenNaissanceKg(BigDecimal v) { this.poidsMoyenNaissanceKg = v; }

    public BigDecimal getPoidsMinNaissanceKg() { return poidsMinNaissanceKg; }
    public void setPoidsMinNaissanceKg(BigDecimal v) { this.poidsMinNaissanceKg = v; }

    public BigDecimal getPoidsMaxNaissanceKg() { return poidsMaxNaissanceKg; }
    public void setPoidsMaxNaissanceKg(BigDecimal v) { this.poidsMaxNaissanceKg = v; }

    public Integer getNbPorceletsAllaites() { return nbPorceletsAllaites; }
    public void setNbPorceletsAllaites(Integer v) { this.nbPorceletsAllaites = v; }

    public LocalDate getDateSevragePrevu() { return dateSevragePrevu; }
    public void setDateSevragePrevu(LocalDate dateSevragePrevu) { this.dateSevragePrevu = dateSevragePrevu; }

    public LocalDate getDateSevrageReel() { return dateSevrageReel; }
    public void setDateSevrageReel(LocalDate dateSevrageReel) { this.dateSevrageReel = dateSevrageReel; }

    public Integer getNbSevres() { return nbSevres; }
    public void setNbSevres(Integer nbSevres) { this.nbSevres = nbSevres; }

    public BigDecimal getPoidsMoyenSevrageKg() { return poidsMoyenSevrageKg; }
    public void setPoidsMoyenSevrageKg(BigDecimal v) { this.poidsMoyenSevrageKg = v; }

    public Integer getDureeLactationJours() { return dureeLactationJours; }
    public void setDureeLactationJours(Integer dureeLactationJours) { this.dureeLactationJours = dureeLactationJours; }

    public Bande getBandeDestination() { return bandeDestination; }
    public void setBandeDestination(Bande bandeDestination) { this.bandeDestination = bandeDestination; }

    public Integer getJoursdepuisPorteePrecedente() { return joursdepuisPorteePrecedente; }
    public void setJoursdepuisPorteePrecedente(Integer v) { this.joursdepuisPorteePrecedente = v; }

    public String getVeterinaireNom() { return veterinaireNom; }
    public void setVeterinaireNom(String veterinaireNom) { this.veterinaireNom = veterinaireNom; }

    public String getOperateurNom() { return operateurNom; }
    public void setOperateurNom(String operateurNom) { this.operateurNom = operateurNom; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getDateCreation() { return dateCreation; }
}
