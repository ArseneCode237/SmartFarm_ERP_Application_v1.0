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

@Entity
@Table(name = "profils_bovins")
public class ProfilBovin {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "animal_id")
    private Animal animal;

    @Enumerated(EnumType.STRING)
    @Column(name = "categorie_bovine", nullable = false)
    private CategorieBovine categorieBovine;

    @Column(length = 100)
    private String race;

    @Column(name = "numero_boucle", length = 30)
    private String numeroBoucle;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_reproductif")
    private StatutReproductifBovin statutReproductif;

    @Column(name = "numero_lactation_actuelle")
    private Integer numeroLactationActuelle = 0;

    @Column(name = "nb_lactations_total")
    private Integer nbLactationsTotal = 0;

    @Column(name = "nb_veaux_total")
    private Integer nbVeauxTotal = 0;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "saillie_active_id")
    private SaillieBovin saillieActive;

    @Column(name = "date_velage_prevu")
    private LocalDate dateVelagePrevu;

    @Column(name = "date_tarissement_prevu")
    private LocalDate dateTarissementPrevu;

    @Column(name = "date_prochaine_saillie_prevue")
    private LocalDate dateProchaineSailliePrevue;

    @Column(name = "production_lait_cumul_kg", precision = 10, scale = 2)
    private BigDecimal productionLaitCumulKg = BigDecimal.ZERO;

    @Column(name = "moyenne_lait_305j_kg", precision = 8, scale = 2)
    private BigDecimal moyenneLait305jKg;

    @Column(name = "date_debut_lactation_actuelle")
    private LocalDate dateDebutLactationActuelle;

    @Column(name = "poids_cible_adulte_kg", precision = 8, scale = 1)
    private BigDecimal poidsCibleAdulteKg;

    @Column(name = "production_lait_cible_j_kg", precision = 6, scale = 2)
    private BigDecimal productionLaitCibleJKg;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bande_origine_id")
    private Bande bandeOrigine;

    @Column(name = "date_extraction_bande")
    private LocalDate dateExtractionBande;

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
    public Animal getAnimal() { return animal; }
    public void setAnimal(Animal animal) { this.animal = animal; }
    public CategorieBovine getCategorieBovine() { return categorieBovine; }
    public void setCategorieBovine(CategorieBovine categorieBovine) { this.categorieBovine = categorieBovine; }
    public String getRace() { return race; }
    public void setRace(String race) { this.race = race; }
    public String getNumeroBoucle() { return numeroBoucle; }
    public void setNumeroBoucle(String numeroBoucle) { this.numeroBoucle = numeroBoucle; }
    public StatutReproductifBovin getStatutReproductif() { return statutReproductif; }
    public void setStatutReproductif(StatutReproductifBovin statutReproductif) { this.statutReproductif = statutReproductif; }
    public Integer getNumeroLactationActuelle() { return numeroLactationActuelle; }
    public void setNumeroLactationActuelle(Integer value) { numeroLactationActuelle = value; }
    public Integer getNbLactationsTotal() { return nbLactationsTotal; }
    public void setNbLactationsTotal(Integer value) { nbLactationsTotal = value; }
    public Integer getNbVeauxTotal() { return nbVeauxTotal; }
    public void setNbVeauxTotal(Integer value) { nbVeauxTotal = value; }
    public SaillieBovin getSaillieActive() { return saillieActive; }
    public void setSaillieActive(SaillieBovin saillieActive) { this.saillieActive = saillieActive; }
    public LocalDate getDateVelagePrevu() { return dateVelagePrevu; }
    public void setDateVelagePrevu(LocalDate value) { dateVelagePrevu = value; }
    public LocalDate getDateTarissementPrevu() { return dateTarissementPrevu; }
    public void setDateTarissementPrevu(LocalDate value) { dateTarissementPrevu = value; }
    public LocalDate getDateProchaineSailliePrevue() { return dateProchaineSailliePrevue; }
    public void setDateProchaineSailliePrevue(LocalDate value) { dateProchaineSailliePrevue = value; }
    public BigDecimal getProductionLaitCumulKg() { return productionLaitCumulKg; }
    public void setProductionLaitCumulKg(BigDecimal value) { productionLaitCumulKg = value; }
    public BigDecimal getMoyenneLait305jKg() { return moyenneLait305jKg; }
    public void setMoyenneLait305jKg(BigDecimal value) { moyenneLait305jKg = value; }
    public LocalDate getDateDebutLactationActuelle() { return dateDebutLactationActuelle; }
    public void setDateDebutLactationActuelle(LocalDate value) { dateDebutLactationActuelle = value; }
    public BigDecimal getPoidsCibleAdulteKg() { return poidsCibleAdulteKg; }
    public void setPoidsCibleAdulteKg(BigDecimal value) { poidsCibleAdulteKg = value; }
    public BigDecimal getProductionLaitCibleJKg() { return productionLaitCibleJKg; }
    public void setProductionLaitCibleJKg(BigDecimal value) { productionLaitCibleJKg = value; }
    public Bande getBandeOrigine() { return bandeOrigine; }
    public void setBandeOrigine(Bande value) { bandeOrigine = value; }
    public LocalDate getDateExtractionBande() { return dateExtractionBande; }
    public void setDateExtractionBande(LocalDate value) { dateExtractionBande = value; }
    public LocalDateTime getDateCreation() { return dateCreation; }
}
