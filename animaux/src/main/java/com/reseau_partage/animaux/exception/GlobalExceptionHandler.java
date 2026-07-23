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
        String rootMessage = ex.getMessage() != null ? ex.getMessage() : "";
        String message = "Requête invalide : ";
        if (rootMessage.contains("Enum")) {
            String enumName = extractBetween(rootMessage, "type `", "`");
            String invalidValue = extractBetween(rootMessage, "from String \"", "\"");
            if (invalidValue == null) invalidValue = extractBetween(rootMessage, "from String '", "'");
            String acceptedValues = enumAcceptedValues(enumName);
            message += "valeur d'enum incorrecte pour le champ " + safeFieldName(rootMessage, enumName) + " : '" + invalidValue + "'.";
            message += " Valeurs acceptées pour " + enumName + " : [" + acceptedValues + "].";
        } else if (rootMessage.contains("JSON parse error")) {
            message += "format JSON invalide. Vérifiez la syntaxe du body.";
        } else if (rootMessage.contains("LocalDate")) {
            message += "format de date invalide. Utilisez le format ISO_LOCAL_DATE (YYYY-MM-DD, ex: 2026-07-21).";
        } else {
            message += rootMessage;
        }
        Map<String, Object> errorResponse = buildErrorResponse(
                400,
                "Bad Request",
                message,
                request.getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    private String extractBetween(String s, String start, String end) {
        int i = s.indexOf(start);
        if (i < 0) return null;
        int from = i + start.length();
        int j = s.indexOf(end, from);
        if (j < 0) return s.substring(from).trim();
        return s.substring(from, j).trim();
    }

    private String safeFieldName(String message, String enumName) {
        String field = extractBetween(message, "field ", " ");
        if (field == null || field.isBlank()) field = extractBetween(message, "property \"", "\"");
        if (field == null || field.isBlank()) field = "`" + enumName + "`";
        return field;
    }

    private String enumAcceptedValues(String enumName) {
        if (enumName == null) return "";
        return switch (enumName) {
            case "Provenance" -> java.util.Arrays.toString(com.reseau_partage.core.entities.Provenance.values());
            case "Sexe" -> java.util.Arrays.toString(com.reseau_partage.core.entities.Sexe.values());
            case "Espece" -> java.util.Arrays.toString(com.reseau_partage.core.entities.Espece.values());
            case "StatutAnimal" -> java.util.Arrays.toString(com.reseau_partage.core.entities.StatutAnimal.values());
            case "StatutBande" -> java.util.Arrays.toString(com.reseau_partage.core.entities.StatutBande.values());
            case "TypeMouvement" -> java.util.Arrays.toString(com.reseau_partage.core.entities.TypeMouvement.values());
            case "ModeSuivi" -> java.util.Arrays.toString(com.reseau_partage.core.entities.ModeSuivi.values());
            case "TypeProduction" -> java.util.Arrays.toString(com.reseau_partage.core.entities.TypeProduction.values());
            case "TypeSaillie" -> java.util.Arrays.toString(com.reseau_partage.core.entities.TypeSaillie.values());
            case "StatutSaillie" -> java.util.Arrays.toString(com.reseau_partage.core.entities.StatutSaillie.values());
            case "TypeMiseBas" -> java.util.Arrays.toString(com.reseau_partage.core.entities.TypeMiseBas.values());
            case "StatutReproductifPorcin" -> java.util.Arrays.toString(com.reseau_partage.core.entities.StatutReproductifPorcin.values());
            case "CauseArchivage" -> java.util.Arrays.toString(com.reseau_partage.core.entities.CauseArchivage.values());
            case "StatutReproducteur" -> java.util.Arrays.toString(com.reseau_partage.core.entities.StatutReproducteur.values());
            case "StatutGestation" -> java.util.Arrays.toString(com.reseau_partage.core.entities.StatutGestation.values());
            case "TypeReproduction" -> java.util.Arrays.toString(com.reseau_partage.core.entities.TypeReproduction.values());
            case "Categorie" -> java.util.Arrays.toString(com.reseau_partage.core.entities.Categorie.values());
            case "TypeDeclaration" -> java.util.Arrays.toString(com.reseau_partage.core.entities.TypeDeclaration.values());
            case "MotifDeclaration" -> java.util.Arrays.toString(com.reseau_partage.core.entities.MotifDeclaration.values());
            case "SourceDeclaration" -> java.util.Arrays.toString(com.reseau_partage.core.entities.SourceDeclaration.values());
            case "StatutDeclaration" -> java.util.Arrays.toString(com.reseau_partage.core.entities.StatutDeclaration.values());
            case "ActionHistorique" -> java.util.Arrays.toString(com.reseau_partage.core.entities.ActionHistorique.values());
            default -> enumName;
        };
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
