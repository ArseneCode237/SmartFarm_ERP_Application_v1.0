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

@Entity
@Table(name = "saillies_bovins")
public class SaillieBovin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vache_id", nullable = false)
    private Animal vache;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taureau_id")
    private Animal taureau;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_saillie", nullable = false)
    private TypeSaillie typeSaillie;

    @Column(name = "date_saillie", nullable = false)
    private LocalDate dateSaillie;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutSaillie statut = StatutSaillie.EN_ATTENTE;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_reproductif_avant_saillie")
    private StatutReproductifBovin statutReproductifAvantSaillie;

    @Column(name = "date_confirmation_echo")
    private LocalDate dateConfirmationEcho;

    @Column(name = "date_velage_prevu")
    private LocalDate dateVelagePrevu;

    @Column(name = "date_pre_velage_prevue")
    private LocalDate datePreVelagePrevue;

    @Column(name = "date_tarissement_prevue")
    private LocalDate dateTarissementPrevue;

    @Column(name = "semence_race", length = 100)
    private String semenceRace;

    @Column(name = "semence_reference", length = 50)
    private String semenceReference;

    @Column(name = "motif_echec", length = 200)
    private String motifEchec;

    @Column(name = "operateur_nom", length = 100)
    private String operateurNom;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Animal getVache() { return vache; }
    public void setVache(Animal value) { vache = value; }
    public Animal getTaureau() { return taureau; }
    public void setTaureau(Animal value) { taureau = value; }
    public TypeSaillie getTypeSaillie() { return typeSaillie; }
    public void setTypeSaillie(TypeSaillie value) { typeSaillie = value; }
    public LocalDate getDateSaillie() { return dateSaillie; }
    public void setDateSaillie(LocalDate value) { dateSaillie = value; }
    public StatutSaillie getStatut() { return statut; }
    public void setStatut(StatutSaillie value) { statut = value; }
    public StatutReproductifBovin getStatutReproductifAvantSaillie() { return statutReproductifAvantSaillie; }
    public void setStatutReproductifAvantSaillie(StatutReproductifBovin value) { statutReproductifAvantSaillie = value; }
    public LocalDate getDateConfirmationEcho() { return dateConfirmationEcho; }
    public void setDateConfirmationEcho(LocalDate value) { dateConfirmationEcho = value; }
    public LocalDate getDateVelagePrevu() { return dateVelagePrevu; }
    public void setDateVelagePrevu(LocalDate value) { dateVelagePrevu = value; }
    public LocalDate getDatePreVelagePrevue() { return datePreVelagePrevue; }
    public void setDatePreVelagePrevue(LocalDate value) { datePreVelagePrevue = value; }
    public LocalDate getDateTarissementPrevue() { return dateTarissementPrevue; }
    public void setDateTarissementPrevue(LocalDate value) { dateTarissementPrevue = value; }
    public String getSemenceRace() { return semenceRace; }
    public void setSemenceRace(String value) { semenceRace = value; }
    public String getSemenceReference() { return semenceReference; }
    public void setSemenceReference(String value) { semenceReference = value; }
    public String getMotifEchec() { return motifEchec; }
    public void setMotifEchec(String value) { motifEchec = value; }
    public String getOperateurNom() { return operateurNom; }
    public void setOperateurNom(String value) { operateurNom = value; }
    public String getNotes() { return notes; }
    public void setNotes(String value) { notes = value; }
}
