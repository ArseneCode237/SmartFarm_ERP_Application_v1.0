package com.reseau_partage.animaux.exception;

/**
 * Levée lorsqu'un ajout d'animaux (bande ou animal individuel) ferait dépasser
 * la capacité maximale d'accueil de la structure d'accueil.
 */
public class CapaciteStructureDepasseeException extends RuntimeException {

    public CapaciteStructureDepasseeException(String nomStructure, int capaciteMax, long occupationActuelle, int effectifAAjouter) {
        super("Capacité maximale de la structure \"" + nomStructure + "\" dépassée : elle peut accueillir au plus "
                + capaciteMax + " animal(aux), " + occupationActuelle + " déjà présent(s)"
                + (effectifAAjouter > 0 ? " et " + effectifAAjouter + " à ajouter" : "")
                + " (" + (occupationActuelle + effectifAAjouter) + " au total).");
    }
}
