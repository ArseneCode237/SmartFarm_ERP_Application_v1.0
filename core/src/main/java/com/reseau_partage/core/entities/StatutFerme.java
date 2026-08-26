package com.reseau_partage.core.entities;

public enum StatutFerme {
    /** Ferme opérationnelle, en fonctionnement normal. */
    ACTIF,
    /** Ferme temporairement à l'arrêt (hors saison, pause). */
    INACTIF,
    /** Ferme en cours de maintenance ou de rénovation. */
    MAINTENANCE,
    /** Ferme définitivement archivée, plus modifiable. */
    ARCHIVEE
}
