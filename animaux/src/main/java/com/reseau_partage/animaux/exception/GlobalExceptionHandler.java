package com.reseau_partage.animaux.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private Map<String, Object> buildErrorResponse(int status, String error, String message, String path) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", status);
        errorResponse.put("error", error);
        errorResponse.put("message", message);
        errorResponse.put("path", path);
        return errorResponse;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> notFound(ResourceNotFoundException ex, HttpServletRequest request) {
        Map<String, Object> errorResponse = buildErrorResponse(
                404,
                "Not Found",
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AnimalDejaExistantException.class)
    public ResponseEntity<Map<String, Object>> conflict(AnimalDejaExistantException ex, HttpServletRequest request) {
        Map<String, Object> errorResponse = buildErrorResponse(
                409,
                "Conflict",
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(TransitionStatutInvalideException.class)
    public ResponseEntity<Map<String, Object>> badRequest(TransitionStatutInvalideException ex, HttpServletRequest request) {
        Map<String, Object> errorResponse = buildErrorResponse(
                400,
                "Bad Request",
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(QuantiteInvalideException.class)
    public ResponseEntity<Map<String, Object>> badRequest(QuantiteInvalideException ex, HttpServletRequest request) {
        Map<String, Object> errorResponse = buildErrorResponse(
                400,
                "Bad Request",
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IncompatibiliteEspeceException.class)
    public ResponseEntity<Map<String, Object>> badRequest(IncompatibiliteEspeceException ex, HttpServletRequest request) {
        Map<String, Object> errorResponse = buildErrorResponse(
                400,
                "Bad Request",
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> badRequest(IllegalArgumentException ex, HttpServletRequest request) {
        Map<String, Object> errorResponse = buildErrorResponse(
                400,
                "Bad Request",
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConfigEspeceIntrouvableException.class)
    public ResponseEntity<Map<String, Object>> notFound(ConfigEspeceIntrouvableException ex, HttpServletRequest request) {
        Map<String, Object> errorResponse = buildErrorResponse(
                404,
                "Not Found",
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(StatutReproducteurInvalideException.class)
    public ResponseEntity<Map<String, Object>> badRequest(StatutReproducteurInvalideException ex, HttpServletRequest request) {
        Map<String, Object> errorResponse = buildErrorResponse(
                400,
                "Bad Request",
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> badRequest(org.springframework.http.converter.HttpMessageNotReadableException ex, HttpServletRequest request) {
        String message = "Requête invalide : ";
        if (ex.getMessage() != null && ex.getMessage().contains("Enum")) {
            message += "valeur d'enum incorrecte. Vérifiez les valeurs acceptées (ex: Provenance={NAISSANCE_INTERNE, ACHAT_EXTERNE, DON, INTERNE, EXTERNE}, Sexe={MALE, FEMELLE, INCONNU}, Espece={POULET, BOVIN, PORC...}).";
        } else if (ex.getMessage() != null && ex.getMessage().contains("JSON parse error")) {
            message += "format JSON invalide. Vérifiez la syntaxe du body.";
        } else if (ex.getMessage() != null && ex.getMessage().contains("LocalDate")) {
            message += "format de date invalide. Utilisez le format ISO_LOCAL_DATE (YYYY-MM-DD, ex: 2026-07-21).";
        } else {
            message += ex.getMessage();
        }
        Map<String, Object> errorResponse = buildErrorResponse(
                400,
                "Bad Request",
                message,
                request.getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        Map<String, Object> errorResponse = buildErrorResponse(
                400,
                "Validation Failed",
                "Erreurs de validation des champs de la requête",
                request.getRequestURI()
        );
        errorResponse.put("errors", errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolationException(ConstraintViolationException ex, HttpServletRequest request) {
        String message = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));

        Map<String, Object> errorResponse = buildErrorResponse(
                400,
                "Validation Failed",
                message,
                request.getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
