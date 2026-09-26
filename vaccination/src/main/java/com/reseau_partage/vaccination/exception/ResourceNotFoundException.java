package com.reseau_partage.vaccination.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource, Object id) {
        super(resource + " introuvable avec l'identifiant: " + id);
    }
}
