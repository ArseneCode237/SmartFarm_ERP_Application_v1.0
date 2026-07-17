package com.reseau_partage.animaux.exception;

import com.reseau_partage.core.entities.StatutAnimal;

public class TransitionStatutInvalideException extends RuntimeException {
    public TransitionStatutInvalideException(StatutAnimal actuel) {
        super("Impossible de modifier un animal avec le statut : " + actuel);
    }
}
