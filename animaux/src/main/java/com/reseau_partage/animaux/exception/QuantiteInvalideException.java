package com.reseau_partage.animaux.exception;

public class QuantiteInvalideException extends RuntimeException {
    public QuantiteInvalideException(int demande, int disponible) {
        super("Quantite demandee (" + demande + ") superieure a l'effectif disponible (" + disponible + ")");
    }
}
