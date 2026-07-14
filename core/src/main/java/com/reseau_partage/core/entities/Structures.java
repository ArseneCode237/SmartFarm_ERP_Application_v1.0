package com.reseau_partage.core.entities;

import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class Structures {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false)
    private String nom;

    // TG | PS | ACCT | PGT | DCP
    @Column(nullable = false)
    private String type;

    private String region;

    @Column(nullable = false)
    private boolean actif = true;

    @CreationTimestamp
    private LocalDateTime dateCreation;


    public Structures() {}

    public Structures(Long id, String nom, String type, String region, boolean actif, LocalDateTime dateCreation) {
        this.id = id;
        this.nom = nom;
        this.type = type;
        this.region = region;
        this.actif = actif;
        this.dateCreation = dateCreation;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    @Override
    public String toString() {
        return "structures{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", type='" + type + '\'' +
                ", region='" + region + '\'' +
                ", actif=" + actif +
                ", dateCreation=" + dateCreation +
                '}';
    }
}
