package com.reseau_partage.organisation.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String entity, Long id) {
        super(entity + " introuvable avec l'id : " + id);
    }
}
