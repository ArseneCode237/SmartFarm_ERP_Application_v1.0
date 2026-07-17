package com.reseau_partage.animaux.exception;

public class AnimalDejaExistantException extends RuntimeException {
    public AnimalDejaExistantException(String code) {
        super("Un animal avec le code " + code + " existe deja");
    }
}
