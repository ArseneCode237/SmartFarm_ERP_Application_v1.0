package com.reseau_partage.core.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "loges")
public class Loge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 30)
    private String code;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "capacite_max_animaux", nullable = false)
    private Integer capaciteMaxAnimaux;

    @Column(name = "superficie_m2", precision = 10, scale = 2)
    private BigDecimal superficieM2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batiment_id", nullable = false)
    private Batiment batiment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bande_id")
    private Bande bande;

    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @PrePersist
    protected void onCreate() {
        this.dateCreation = LocalDateTime.now();
        this.dateModification = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.dateModification = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getCapaciteMaxAnimaux() { return capaciteMaxAnimaux; }
    public void setCapaciteMaxAnimaux(Integer capaciteMaxAnimaux) { this.capaciteMaxAnimaux = capaciteMaxAnimaux; }

    public BigDecimal getSuperficieM2() { return superficieM2; }
    public void setSuperficieM2(BigDecimal superficieM2) { this.superficieM2 = superficieM2; }

    public Batiment getBatiment() { return batiment; }
    public void setBatiment(Batiment batiment) { this.batiment = batiment; }

    public Bande getBande() { return bande; }
    public void setBande(Bande bande) { this.bande = bande; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public LocalDateTime getDateModification() { return dateModification; }
    public void setDateModification(LocalDateTime dateModification) { this.dateModification = dateModification; }
}
