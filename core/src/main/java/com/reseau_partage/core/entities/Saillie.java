package com.reseau_partage.core.entities;

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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

/**
 * Enregistrement d'une saillie (naturelle ou IA) d'une truie.
 * Chaque saillie peut aboutir à une MiseBas si la gestation est confirmée.
 */
@Entity
@Table(name = "saillies")
public class Saillie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ── Protagonistes ────────────────────────────────────────────────────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "truie_id", nullable = false)
    private Animal truie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verrat_id")
    private Animal verrat;              // null si insémination artificielle

    @Enumerated(EnumType.STRING)
    @Column(name = "type_saillie", nullable = false)
    private TypeSaillie typeSaillie;

    // ── Numérotation ──────────────────────────────────────────────────────────
    /** Numéro de saillie dans la carrière de cette truie (1ère, 2ème…). */
    @Column(name = "numero_saillie_carriere", nullable = false)
    private Integer numeroSaillieCarriere;

    /** Numéro de portée correspondante dans la carrière (1ère portée, 2ème…). */
    @Column(name = "numero_portee_correspondante")
    private Integer numeroPorteeCorrespondante;

    // ── Dates saillie ─────────────────────────────────────────────────────────
    @Column(name = "date_saillie", nullable = false)
    private LocalDate dateSaillie;

    /** Deuxième passage, 12h après le premier. */
    @Column(name = "date_deuxieme_saillie")
    private LocalDate dateDeuxiemeSaillie;

    // ── Résultat ──────────────────────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private StatutSaillie statut = StatutSaillie.EN_ATTENTE;

    @Column(name = "date_confirmation_echo")
    private LocalDate dateConfirmationEcho;

    @Column(name = "date_infirmation")
    private LocalDate dateInfirmation;

    @Column(name = "motif_echec", length = 200)
    private String motifEchec;

    // ── Dates calculées ───────────────────────────────────────────────────────
    /** dateSaillie + 114 jours (durée gestation porcine). */
    @Column(name = "date_mise_bas_prevue")
    private LocalDate dateMiseBasPrevue;

    /** dateMiseBasPrevue - 7 jours (transfert en loge maternité). */
    @Column(name = "date_transfert_maternite_prevue")
    private LocalDate dateTransfertMaternitePrevue;

    // ── Insémination artificielle ─────────────────────────────────────────────
    @Column(name = "semence_fournisseur", length = 100)
    private String semenceFournisseur;

    @Column(name = "semence_reference", length = 50)
    private String semenceReference;

    // ── Opérateur ─────────────────────────────────────────────────────────────
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

    public Animal getTruie() { return truie; }
    public void setTruie(Animal truie) { this.truie = truie; }

    public Animal getVerrat() { return verrat; }
    public void setVerrat(Animal verrat) { this.verrat = verrat; }

    public TypeSaillie getTypeSaillie() { return typeSaillie; }
    public void setTypeSaillie(TypeSaillie typeSaillie) { this.typeSaillie = typeSaillie; }

    public Integer getNumeroSaillieCarriere() { return numeroSaillieCarriere; }
    public void setNumeroSaillieCarriere(Integer v) { this.numeroSaillieCarriere = v; }

    public Integer getNumeroPorteeCorrespondante() { return numeroPorteeCorrespondante; }
    public void setNumeroPorteeCorrespondante(Integer v) { this.numeroPorteeCorrespondante = v; }

    public LocalDate getDateSaillie() { return dateSaillie; }
    public void setDateSaillie(LocalDate dateSaillie) { this.dateSaillie = dateSaillie; }

    public LocalDate getDateDeuxiemeSaillie() { return dateDeuxiemeSaillie; }
    public void setDateDeuxiemeSaillie(LocalDate v) { this.dateDeuxiemeSaillie = v; }

    public StatutSaillie getStatut() { return statut; }
    public void setStatut(StatutSaillie statut) { this.statut = statut; }

    public LocalDate getDateConfirmationEcho() { return dateConfirmationEcho; }
    public void setDateConfirmationEcho(LocalDate v) { this.dateConfirmationEcho = v; }

    public LocalDate getDateInfirmation() { return dateInfirmation; }
    public void setDateInfirmation(LocalDate v) { this.dateInfirmation = v; }

    public String getMotifEchec() { return motifEchec; }
    public void setMotifEchec(String motifEchec) { this.motifEchec = motifEchec; }

    public LocalDate getDateMiseBasPrevue() { return dateMiseBasPrevue; }
    public void setDateMiseBasPrevue(LocalDate dateMiseBasPrevue) { this.dateMiseBasPrevue = dateMiseBasPrevue; }

    public LocalDate getDateTransfertMaternitePrevue() { return dateTransfertMaternitePrevue; }
    public void setDateTransfertMaternitePrevue(LocalDate v) { this.dateTransfertMaternitePrevue = v; }

    public String getSemenceFournisseur() { return semenceFournisseur; }
    public void setSemenceFournisseur(String v) { this.semenceFournisseur = v; }

    public String getSemenceReference() { return semenceReference; }
    public void setSemenceReference(String v) { this.semenceReference = v; }

    public String getOperateurNom() { return operateurNom; }
    public void setOperateurNom(String operateurNom) { this.operateurNom = operateurNom; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getDateCreation() { return dateCreation; }
}
