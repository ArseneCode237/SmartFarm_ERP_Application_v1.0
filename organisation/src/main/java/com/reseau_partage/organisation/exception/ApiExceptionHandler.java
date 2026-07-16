package com.reseau_partage.organisation.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException e,
            HttpServletRequest request) {
        return error(HttpStatus.NOT_FOUND, e.getMessage(), request);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Map<String, Object>> handleConflict(ConflictException e,
            HttpServletRequest request) {
        return error(HttpStatus.CONFLICT, e.getMessage(), request);
    }

    @ExceptionHandler({StatutTransitionException.class, IllegalArgumentException.class})
    public ResponseEntity<Map<String, Object>> handleBadRequest(RuntimeException e,
            HttpServletRequest request) {
        return error(HttpStatus.BAD_REQUEST, e.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException e,
            HttpServletRequest request) {
        StringBuilder message = new StringBuilder("Certaines données sont invalides :");
        e.getBindingResult().getFieldErrors().forEach(fe ->
                message.append(" [").append(fe.getField()).append("] ")
                       .append(fe.getDefaultMessage()).append(";"));
        return error(HttpStatus.BAD_REQUEST, message.toString(), request);
    }

    /**
     * Attrape les violations de contraintes PostgreSQL (unique, check, length…)
     * et retourne un message lisible plutôt qu'un 500 générique.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException e,
            HttpServletRequest request) {
        log.error("Violation de contrainte base de données : {}", e.getMostSpecificCause().getMessage());

        String cause = e.getMostSpecificCause().getMessage();
        String message;

        if (cause != null && cause.contains("too long")) {
            message = "Une valeur saisie est trop longue pour le champ correspondant.";
        } else if (cause != null && cause.contains("unique") || cause != null && cause.contains("already exists")) {
            message = "Cette valeur existe déjà en base de données.";
        } else if (cause != null && cause.contains("check constraint")) {
            message = "Une valeur ne respecte pas les contraintes définies.";
        } else if (cause != null && cause.contains("not-null") || cause != null && cause.contains("null value")) {
            message = "Un champ obligatoire est manquant.";
        } else {
            message = "Erreur de cohérence des données. Vérifiez les valeurs saisies.";
        }

        return error(HttpStatus.UNPROCESSABLE_ENTITY, message, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception e,
            HttpServletRequest request) {
        log.error("Erreur inattendue sur {} {} : {}", request.getMethod(), request.getRequestURI(), e.getMessage(), e);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Une erreur interne est survenue.", request);
    }

    private ResponseEntity<Map<String, Object>> error(HttpStatus status, String message,
            HttpServletRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }
}
