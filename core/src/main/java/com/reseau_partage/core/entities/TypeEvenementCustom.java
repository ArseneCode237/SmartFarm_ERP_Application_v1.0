package com.reseau_partage.core.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "types_evenements_custom")
public class TypeEvenementCustom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Espece espece;

    @Column(nullable = false, length = 100)
    private String libelle;

    @Column(length = 255)
    private String description;

    @Column(nullable = false)
    private Boolean actif = true;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Espece getEspece() { return espece; }
    public void setEspece(Espece espece) { this.espece = espece; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Boolean getActif() { return actif; }
    public void setActif(Boolean actif) { this.actif = actif; }
}
