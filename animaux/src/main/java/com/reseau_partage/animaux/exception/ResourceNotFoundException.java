package com.reseau_partage.animaux.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource, Object id) {
        super(resource + " non trouve avec l'identifiant: " + id);
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
