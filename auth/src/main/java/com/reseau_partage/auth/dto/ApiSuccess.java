package com.reseau_partage.auth.dto;

/**
 * Réponse commune pour les opérations qui ne retournent pas de données métier.
 */
public record ApiSuccess(boolean success, int status, String message) {

    public static ApiSuccess ok(String message) {
        return new ApiSuccess(true, 200, message);
    }
}
