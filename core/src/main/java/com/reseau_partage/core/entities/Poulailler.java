package com.reseau_partage.core.entities;

import jakarta.persistence.*;

/**
 * Sous-type de Structure pour les poulaillers (aviculture).
 * Discriminator value : "POULAILLER"
 */
@Entity
@DiscriminatorValue("POULAILLER")
public class Poulailler extends Structure {

    /** Nombre maximum de volailles pouvant être logées. */
    private Integer capaciteMaxAnimaux;

    /** Type de ventilation : NATURELLE, FORCEE, TUNNEL. */
    @Column(length = 50)
    private String typeVentilation;

    /** Durée du vide sanitaire recommandée en jours. */
    private Integer dureeVideSanitaireJours;

    /** Nombre de rangées de perchoirs ou de cages. */
    private Integer nombreRangees;

    /** Système d'abreuvement : MANUEL, NIPPONS, AUTOMATIQUE. */
    @Column(length = 50)
    private String systemeAbreuvement;

    /** Type de production : PONTE, CHAIR, MIXTE. */
    @Column(name = "type_production", length = 50)
    private String typeProduction;

    /** Système de chauffage utilisé en saison froide. */
    @Column(name = "systeme_chauffage", length = 50)
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
