package com.reseau_partage.core.entities;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Sous-type de Structure pour les porcheries (élevage porcin).
 * Discriminator value : "PORCHERIE"
 */
@Entity
@DiscriminatorValue("PORCHERIE")
public class Porcherie extends Structure {

    /** Nombre maximum de porcs pouvant être logés. */
    private Integer capaciteMaxAnimaux;

    /** Type de sol : CAILLEBOTIS, BETON, LITIERE. */
    @Column(name = "type_sol", length = 50)
    private String typeSol;

    /** Durée du vide sanitaire recommandée en jours. */
    private Integer dureeVideSanitaireJours;

    /** Système d'abreuvement : MANUEL, AUTOMATIQUE, PIPETTE. */
    @Column(length = 50)
    private String systemeAbreuvement;

    /** Système d'évacuation des déjections : LISIER, FUMIER, BIOGAZ. */
    @Column(name = "systeme_evacuation", length = 50)
    private String systemeEvacuation;

    /** Nombre de cases (loges) dans la porcherie. */
    @Column(name = "nombre_cases")
    private Integer nombreCases;

    /** Type de ventilation : NATURELLE, FORCEE. */
    @Column(length = 50)
    private String typeVentilation;

    // ── Getters / Setters ────────────────────────────────────────────────────

    public Integer getCapaciteMaxAnimaux() { return capaciteMaxAnimaux; }
    public void setCapaciteMaxAnimaux(Integer v) { capaciteMaxAnimaux = v; }

    public String getTypeSol() { return typeSol; }
    public void setTypeSol(String v) { typeSol = v; }

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
