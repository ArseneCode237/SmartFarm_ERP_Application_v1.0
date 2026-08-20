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
     *
     * La détection s'appuie d'abord sur le code SQLState (indépendant de la langue
     * du serveur), puis en secours sur le texte du message (français ou anglais).
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException e,
            HttpServletRequest request) {
        log.error("Violation de contrainte base de données : {}", e.getMostSpecificCause().getMessage());

        Throwable cause = e.getMostSpecificCause();
        String sqlState = cause instanceof java.sql.SQLException se ? se.getSQLState() : null;
        String message = switch (sqlState == null ? "" : sqlState) {
            case "22001", "22003" -> "Une valeur saisie est trop longue pour le champ correspondant.";
            case "23505" -> "Cette valeur existe déjà en base de données.";
            case "23514" -> "Une valeur ne respecte pas les contraintes définies.";
            case "23502" -> "Un champ obligatoire est manquant.";
            case "23503" -> "Une valeur saisie ne correspond à aucun élément existant.";
            default -> fromCauseText(cause);
        };

        return error(HttpStatus.UNPROCESSABLE_ENTITY, message, request);
    }

    private String fromCauseText(Throwable cause) {
        String text = cause == null ? null : cause.getMessage();
        if (text == null)
            return "Erreur de cohérence des données. Vérifiez les valeurs saisies.";
        String lower = text.toLowerCase();
        if (lower.contains("too long") || lower.contains("trop longue") || lower.contains("out of range"))
            return "Une valeur saisie est trop longue pour le champ correspondant.";
        if (lower.contains("unique") || lower.contains("already exists") || lower.contains("existe déjà") || lower.contains("dupliquée") || lower.contains("duplicate"))
            return "Cette valeur existe déjà en base de données.";
        if (lower.contains("check constraint") || lower.contains("contrainte de contrôle") || lower.contains("contrainte de verification"))
            return "Une valeur ne respecte pas les contraintes définies.";
        if (lower.contains("not-null") || lower.contains("null value") || lower.contains("non-nullité") || lower.contains("non-nullite"))
            return "Un champ obligatoire est manquant.";
        return "Erreur de cohérence des données. Vérifiez les valeurs saisies.";
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception e,
            HttpServletRequest request) {
        log.error("Erreur inattendue sur {} {} : {}", request.getMethod(), request.getRequestURI(), e.getMessage(), e);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        body.put("message", "Une erreur interne est survenue.");
        body.put("exception", e.getClass().getName() + ": " + (e.getMessage() == null ? "" : e.getMessage()));
        body.put("path", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
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
