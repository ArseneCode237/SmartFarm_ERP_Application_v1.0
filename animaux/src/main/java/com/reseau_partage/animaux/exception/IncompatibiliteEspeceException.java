package com.reseau_partage.animaux.exception;

public class IncompatibiliteEspeceException extends RuntimeException {
    public IncompatibiliteEspeceException(String contexte) {
        super("Incompatibilite d'espece : " + contexte);
    }
}
