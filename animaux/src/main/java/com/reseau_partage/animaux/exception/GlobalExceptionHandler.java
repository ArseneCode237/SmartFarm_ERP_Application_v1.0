package com.reseau_partage.animaux.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> notFound(ResourceNotFoundException ex) {
        return Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 404,
                "error", "Not Found",
                "message", ex.getMessage(),
                "path", ""
        );
    }

    @ExceptionHandler(AnimalDejaExistantException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Object> conflict(AnimalDejaExistantException ex) {
        return Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 409,
                "error", "Conflict",
                "message", ex.getMessage(),
                "path", ""
        );
    }

    @ExceptionHandler(TransitionStatutInvalideException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> badRequest(TransitionStatutInvalideException ex) {
        return Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 400,
                "error", "Bad Request",
                "message", ex.getMessage(),
                "path", ""
        );
    }

    @ExceptionHandler(QuantiteInvalideException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> badRequest(QuantiteInvalideException ex) {
        return Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 400,
                "error", "Bad Request",
                "message", ex.getMessage(),
                "path", ""
        );
    }

    @ExceptionHandler(IncompatibiliteEspeceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> badRequest(IncompatibiliteEspeceException ex) {
        return Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 400,
                "error", "Bad Request",
                "message", ex.getMessage(),
                "path", ""
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> badRequest(IllegalArgumentException ex) {
        return Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 400,
                "error", "Bad Request",
                "message", ex.getMessage(),
                "path", ""
        );
    }

    @ExceptionHandler(ConfigEspeceIntrouvableException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> notFound(ConfigEspeceIntrouvableException ex) {
        return Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 404,
                "error", "Not Found",
                "message", ex.getMessage(),
                "path", ""
        );
    }

    @ExceptionHandler(StatutReproducteurInvalideException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> badRequest(StatutReproducteurInvalideException ex) {
        return Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 400,
                "error", "Bad Request",
                "message", ex.getMessage(),
                "path", ""
        );
    }

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> badRequest(org.springframework.http.converter.HttpMessageNotReadableException ex) {
        String message = "Requête invalide : ";
        if (ex.getMessage() != null && ex.getMessage().contains("Enum")) {
            message += "valeur d'enum incorrecte. Vérifiez les valeurs acceptées (ex: Provenance={NAISSANCE_INTERNE, ACHAT_EXTERNE, DON, INTERNE, EXTERNE}, Sexe={MALE, FEMELLE, INCONNU}, Espece={POULET, BOVIN, PORC...}).";
        } else if (ex.getMessage() != null && ex.getMessage().contains("JSON parse error")) {
            message += "format JSON invalide. Vérifiez la syntaxe du body.";
        } else {
            message += ex.getMessage();
        }
        return Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 400,
                "error", "Bad Request",
                "message", message,
                "path", ""
        );
    }
}
