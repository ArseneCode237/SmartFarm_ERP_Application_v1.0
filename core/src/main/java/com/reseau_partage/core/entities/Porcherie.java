package com.reseau_partage.core.entities;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Sous-type de Structure pour les porcheries (élevage porcin).
 * Discriminator value : "PORCHERIE"
 *
 * Colonnes partagées avec d'autres sous-types (SINGLE_TABLE) :
 *   capacite_max_animaux, type_ventilation, duree_vide_sanitaire_jours,
 *   systeme_abreuvement, type_sol
 * Colonnes propres à la porcherie :
 *   systeme_evacuation, nombre_cases
 */
@Entity
@DiscriminatorValue("PORCHERIE")
public class Porcherie extends Structure {

    // Partagé avec Batiment, Enclos, Poulailler
    private Integer capaciteMaxAnimaux;

    // Partagé avec Batiment, Poulailler
    private Integer dureeVideSanitaireJours;

    // Partagé avec Batiment, Poulailler — @Column(length=50) cohérent
    @Column(length = 50)
    private String systemeAbreuvement;

    // Propre à la Porcherie : LISIER | FUMIER | BIOGAZ
    // Hibernate mappe systemeEvacuation → systeme_evacuation automatiquement
    @Column(length = 50)
    private String systemeEvacuation;

    // Propre à la Porcherie
    // Hibernate mappe nombreCases → nombre_cases automatiquement
    private Integer nombreCases;

    // Partagé avec Batiment, Poulailler — @Column(length=50) cohérent
    @Column(length = 50)
    private String typeVentilation;

    // ── Getters / Setters ────────────────────────────────────────────────────

    public Integer getCapaciteMaxAnimaux() { return capaciteMaxAnimaux; }
    public void setCapaciteMaxAnimaux(Integer v) { capaciteMaxAnimaux = v; }

    public Integer getDureeVideSanitaireJours() { return dureeVideSanitaireJours; }
    public void setDureeVideSanitaireJours(Integer v) { dureeVideSanitaireJours = v; }

    public String getSystemeAbreuvement() { return systemeAbreuvement; }
    public void setSystemeAbreuvement(String v) { systemeAbreuvement = v; }

    public String getSystemeEvacuation() { return systemeEvacuation; }
    public void setSystemeEvacuation(String v) { systemeEvacuation = v; }

    public Integer getNombreCases() { return nombreCases; }
    public void setNombreCases(Integer v) { nombreCases = v; }

    public String getTypeVentilation() { return typeVentilation; }
    public void setTypeVentilation(String v) { typeVentilation = v; }
}
