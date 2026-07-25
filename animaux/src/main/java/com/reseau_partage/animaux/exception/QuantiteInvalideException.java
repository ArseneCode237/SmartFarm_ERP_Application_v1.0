package com.reseau_partage.animaux.exception;

public class QuantiteInvalideException extends RuntimeException {
    public QuantiteInvalideException(int demande, int disponible) {
        super("Quantité demandée (" + demande + ") supérieure à l'effectif disponible (" + disponible + "). Impossible de traiter cette opération.");
    }
}
