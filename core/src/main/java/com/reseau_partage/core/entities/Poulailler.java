package com.reseau_partage.core.entities;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Sous-type de Structure pour les poulaillers (aviculture).
 * Discriminator value : "POULAILLER"
 *
 * Colonnes partagées avec d'autres sous-types (SINGLE_TABLE) :
 *   capacite_max_animaux, type_ventilation, duree_vide_sanitaire_jours,
 *   nombre_rangees, systeme_abreuvement
 * Colonnes propres au poulailler :
 *   type_production, systeme_chauffage
 */
@Entity
@DiscriminatorValue("POULAILLER")
public class Poulailler extends Structure {

    // Partagé avec Batiment, Enclos, Porcherie
    private Integer capaciteMaxAnimaux;

    // Partagé avec Batiment, Porcherie — @Column(length=50) cohérent
    @Column(length = 50)
    private String typeVentilation;

    // Partagé avec Batiment, Porcherie
    private Integer dureeVideSanitaireJours;

    // Partagé avec Batiment
    private Integer nombreRangees;

    // Partagé avec Batiment, Porcherie — @Column(length=50) cohérent
    @Column(length = 50)
    private String systemeAbreuvement;

    // Propre au Poulailler : PONTE | CHAIR | MIXTE
    // Hibernate mappe typeProduction → type_production automatiquement
    @Column(length = 50)
    private String typeProduction;

    // Propre au Poulailler
    // Hibernate mappe systemeChauffage → systeme_chauffage automatiquement
    @Column(length = 50)
    private String systemeChauffage;

    // ── Getters / Setters ────────────────────────────────────────────────────

    public Integer getCapaciteMaxAnimaux() { return capaciteMaxAnimaux; }
    public void setCapaciteMaxAnimaux(Integer v) { capaciteMaxAnimaux = v; }

    public String getTypeVentilation() { return typeVentilation; }
    public void setTypeVentilation(String v) { typeVentilation = v; }

    public Integer getDureeVideSanitaireJours() { return dureeVideSanitaireJours; }
    public void setDureeVideSanitaireJours(Integer v) { dureeVideSanitaireJours = v; }

    public Integer getNombreRangees() { return nombreRangees; }
    public void setNombreRangees(Integer v) { nombreRangees = v; }

    public String getSystemeAbreuvement() { return systemeAbreuvement; }
    public void setSystemeAbreuvement(String v) { systemeAbreuvement = v; }

    public String getTypeProduction() { return typeProduction; }
    public void setTypeProduction(String v) { typeProduction = v; }

    public String getSystemeChauffage() { return systemeChauffage; }
    public void setSystemeChauffage(String v) { systemeChauffage = v; }
}
