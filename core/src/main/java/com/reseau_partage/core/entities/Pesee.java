package com.reseau_partage.core.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "pesees")
public class Pesee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_id")
    private Animal animal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bande_id")
    private Bande bande;

    @Column(name = "date_pesee", nullable = false)
    private LocalDate datePesee;

    @Column(name = "age_jours_au_moment_pesee")
    private Integer ageJoursAuMomentPesee;

    @Column(name = "poids_kg", precision = 8, scale = 3, nullable = false)
    private BigDecimal poidsKg;

    @Column(name = "gain_depuis_derniere_pesee_kg", precision = 8, scale = 3)
    private BigDecimal gainDepuisDernierePeseeKg;

    @Column(name = "gmq_g", precision = 8, scale = 3)
    private BigDecimal gmqG;

    @Column(name = "ecart_courbe_reference_pct", precision = 5, scale = 2)
    private BigDecimal ecartCourbeReferencePct;

    @Column(name = "sous_performeur")
    private Boolean sousPerformeur;

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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Animal getAnimal() { return animal; }
    public void setAnimal(Animal animal) { this.animal = animal; }
    public Bande getBande() { return bande; }
    public void setBande(Bande bande) { this.bande = bande; }
    public LocalDate getDatePesee() { return datePesee; }
    public void setDatePesee(LocalDate datePesee) { this.datePesee = datePesee; }
    public Integer getAgeJoursAuMomentPesee() { return ageJoursAuMomentPesee; }
    public void setAgeJoursAuMomentPesee(Integer ageJoursAuMomentPesee) { this.ageJoursAuMomentPesee = ageJoursAuMomentPesee; }
    public BigDecimal getPoidsKg() { return poidsKg; }
    public void setPoidsKg(BigDecimal poidsKg) { this.poidsKg = poidsKg; }
    public BigDecimal getGainDepuisDernierePeseeKg() { return gainDepuisDernierePeseeKg; }
    public void setGainDepuisDernierePeseeKg(BigDecimal gainDepuisDernierePeseeKg) { this.gainDepuisDernierePeseeKg = gainDepuisDernierePeseeKg; }
    public BigDecimal getGmqG() { return gmqG; }
    public void setGmqG(BigDecimal gmqG) { this.gmqG = gmqG; }
    public BigDecimal getEcartCourbeReferencePct() { return ecartCourbeReferencePct; }
    public void setEcartCourbeReferencePct(BigDecimal ecartCourbeReferencePct) { this.ecartCourbeReferencePct = ecartCourbeReferencePct; }
    public Boolean getSousPerformeur() { return sousPerformeur; }
    public void setSousPerformeur(Boolean sousPerformeur) { this.sousPerformeur = sousPerformeur; }
    public String getOperateurNom() { return operateurNom; }
    public void setOperateurNom(String operateurNom) { this.operateurNom = operateurNom; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public LocalDateTime getDateCreation() { return dateCreation; }
}
