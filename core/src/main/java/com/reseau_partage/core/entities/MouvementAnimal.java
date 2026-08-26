package com.reseau_partage.core.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "mouvements_animaux")
public class MouvementAnimal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_id")
    private Animal animal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bande_id")
    private Bande bande;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_mouvement", nullable = false)
    private TypeMouvement typeMouvement;

    @Column(name = "date_mouvement", nullable = false)
    private LocalDate dateMouvement;

    @Column(name = "quantite")
    private Integer quantite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "structure_origine_id")
    private Structure structureOrigine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "structure_destination_id")
    private Structure structureDestination;

    @Column(name = "poids_kg", precision = 8, scale = 3)
    private BigDecimal poidsKg;

    @Column(name = "prix_unitaire", precision = 12, scale = 2)
    private BigDecimal prixUnitaire;

    @Column(columnDefinition = "TEXT")
    private String motif;

    @Column(name = "operateur_nom", length = 100)
    private String operateurNom;

    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    @PrePersist
    protected void onCreate() { this.dateCreation = LocalDateTime.now(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Animal getAnimal() { return animal; }
    public void setAnimal(Animal animal) { this.animal = animal; }
    public Bande getBande() { return bande; }
    public void setBande(Bande bande) { this.bande = bande; }
    public TypeMouvement getTypeMouvement() { return typeMouvement; }
    public void setTypeMouvement(TypeMouvement typeMouvement) { this.typeMouvement = typeMouvement; }
    public LocalDate getDateMouvement() { return dateMouvement; }
    public void setDateMouvement(LocalDate dateMouvement) { this.dateMouvement = dateMouvement; }
    public Integer getQuantite() { return quantite; }
    public void setQuantite(Integer quantite) { this.quantite = quantite; }
    public Structure getStructureOrigine() { return structureOrigine; }
    public void setStructureOrigine(Structure structureOrigine) { this.structureOrigine = structureOrigine; }
    public Structure getStructureDestination() { return structureDestination; }
    public void setStructureDestination(Structure structureDestination) { this.structureDestination = structureDestination; }
    public BigDecimal getPoidsKg() { return poidsKg; }
    public void setPoidsKg(BigDecimal poidsKg) { this.poidsKg = poidsKg; }
    public BigDecimal getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(BigDecimal prixUnitaire) { this.prixUnitaire = prixUnitaire; }
    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }
    public String getOperateurNom() { return operateurNom; }
    public void setOperateurNom(String operateurNom) { this.operateurNom = operateurNom; }
    public LocalDateTime getDateCreation() { return dateCreation; }
}
