package com.reseau_partage.core.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "evenements_reproduction")
public class EvenementReproduction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "femelle_id", nullable = false)
    private Animal femelle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "male_id")
    private Animal male;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeReproduction type;

    @Column(name = "date_saillie", nullable = false)
    private LocalDate dateSaillie;

    @Column(name = "date_mise_bas_prevue")
    private LocalDate dateMiseBasPrevue;

    @Column(name = "date_mise_bas_reelle")
    private LocalDate dateMiseBasReelle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutGestation statut = StatutGestation.EN_ATTENTE;

    private Integer nombreNesVivants;
    private Integer nombreNesMorts;
    private BigDecimal poidsMoyenNaissanceKg;

    @Column(name = "date_sevrage_prevu")
    private LocalDate dateSevragePrevu;

    @Column(name = "date_sevrage_reel")
    private LocalDate dateSevrageReel;

    @Column(name = "poids_au_sevrage_kg", precision = 8, scale = 3)
    private BigDecimal poidsAuSevrageKg;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    @PrePersist
    protected void onCreate() {
        this.dateCreation = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Animal getFemelle() { return femelle; }
    public void setFemelle(Animal femelle) { this.femelle = femelle; }
    public Animal getMale() { return male; }
    public void setMale(Animal male) { this.male = male; }
    public TypeReproduction getType() { return type; }
    public void setType(TypeReproduction type) { this.type = type; }
    public LocalDate getDateSaillie() { return dateSaillie; }
    public void setDateSaillie(LocalDate dateSaillie) { this.dateSaillie = dateSaillie; }
    public LocalDate getDateMiseBasPrevue() { return dateMiseBasPrevue; }
    public void setDateMiseBasPrevue(LocalDate dateMiseBasPrevue) { this.dateMiseBasPrevue = dateMiseBasPrevue; }
    public LocalDate getDateMiseBasReelle() { return dateMiseBasReelle; }
    public void setDateMiseBasReelle(LocalDate dateMiseBasReelle) { this.dateMiseBasReelle = dateMiseBasReelle; }
    public StatutGestation getStatut() { return statut; }
    public void setStatut(StatutGestation statut) { this.statut = statut; }
    public Integer getNombreNesVivants() { return nombreNesVivants; }
    public void setNombreNesVivants(Integer nombreNesVivants) { this.nombreNesVivants = nombreNesVivants; }
    public Integer getNombreNesMorts() { return nombreNesMorts; }
    public void setNombreNesMorts(Integer nombreNesMorts) { this.nombreNesMorts = nombreNesMorts; }
    public BigDecimal getPoidsMoyenNaissanceKg() { return poidsMoyenNaissanceKg; }
    public void setPoidsMoyenNaissanceKg(BigDecimal poidsMoyenNaissanceKg) { this.poidsMoyenNaissanceKg = poidsMoyenNaissanceKg; }
    public LocalDate getDateSevragePrevu() { return dateSevragePrevu; }
    public void setDateSevragePrevu(LocalDate dateSevragePrevu) { this.dateSevragePrevu = dateSevragePrevu; }
    public LocalDate getDateSevrageReel() { return dateSevrageReel; }
    public void setDateSevrageReel(LocalDate dateSevrageReel) { this.dateSevrageReel = dateSevrageReel; }
    public BigDecimal getPoidsAuSevrageKg() { return poidsAuSevrageKg; }
    public void setPoidsAuSevrageKg(BigDecimal poidsAuSevrageKg) { this.poidsAuSevrageKg = poidsAuSevrageKg; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public LocalDateTime getDateCreation() { return dateCreation; }
}
