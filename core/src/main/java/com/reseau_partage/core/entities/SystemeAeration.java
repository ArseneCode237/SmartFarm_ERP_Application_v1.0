package com.reseau_partage.core.entities;

public enum SystemeAeration {
    /** Aucun système précisé (valeur par défaut du formulaire). */
    NON_SPECIFIE,
    /** Turbine rotative de surface. */
    TURBINE,
    /** Aération naturelle — échanges air/eau sans équipement. */
    NATUREL,
    /** Roue à aubes (paddlewheel). */
    PADDLEWHEEL,
    /** Diffuseur à bulles fines en fond de bassin. */
    DIFFUSEUR,
    /** Fontaine aératrice. */
    FONTAINE
}
