package com.reseau_partage.organisation.exception;

import com.reseau_partage.core.entities.StatutStructure;

public class StatutTransitionException extends RuntimeException {
    public StatutTransitionException(StatutStructure from, StatutStructure to) {
        super("Transition de statut invalide : " + from + " vers " + to);
    }
}
