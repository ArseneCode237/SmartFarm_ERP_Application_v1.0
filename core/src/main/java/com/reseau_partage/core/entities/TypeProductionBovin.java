package com.reseau_partage.core.entities;

/**
 * Type de production d'une bande bovine (troupeau).
 * Complète l'enum générique {@link TypeProduction} avec les valeurs propres aux bovins.
 */
public enum TypeProductionBovin {
    /** Bovins charcutiers conduits en bande pour l'engraissement. */
    ENGRAISSEMENT,
    /** Vaches allaitantes et leurs veaux suivis en bande. */
    ALLAITANT,
    /** Petit troupeau laitier suivi collectivement. */
    LAITIER_COLLECTIF,
    /** Production mixte (lait et viande). */
    MIXTE
}
