package com.reseau_partage.animaux.controller;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.reseau_partage.animaux.dto.declaration.DeclarationRequest;
import com.reseau_partage.animaux.dto.declaration.DeclarationResponse;
import com.reseau_partage.animaux.dto.declaration.DeclarationStatsResponse;
import com.reseau_partage.animaux.service.DeclarationService;
import com.reseau_partage.core.entities.StatutDeclaration;
import com.reseau_partage.core.entities.TypeDeclaration;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/animaux/declarations")
public class DeclarationController {

    private final DeclarationService declarationService;

    public DeclarationController(DeclarationService declarationService) {
        this.declarationService = declarationService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> creer(@Valid @RequestBody DeclarationRequest request, Authentication authentication) {
        String username = authentication != null ? authentication.getName() : "system";
        DeclarationResponse response = declarationService.creerDeclaration(request, null, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "data", response,
                "message", "Declaration creee avec succes pour la bande id=" + request.bandeId()
        ));
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> listerToutes(
            @RequestParam(required = false) Long fermeId,
            @RequestParam(required = false) TypeDeclaration type,
            @RequestParam(required = false) StatutDeclaration statut,
            @RequestParam(required = false) LocalDate dateDebut,
            @RequestParam(required = false) LocalDate dateFin,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "dateDeclaration") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<DeclarationResponse> declarations = declarationService.listerToutes(fermeId, type, statut, dateDebut, dateFin, pageable);

        return ResponseEntity.ok(Map.of(
                "content", declarations.getContent(),
                "totalElements", declarations.getTotalElements(),
                "totalPages", declarations.getTotalPages()
        ));
    }

    @GetMapping("/bande/{bandeId}")
    public ResponseEntity<Map<String, Object>> listerParBande(
            @PathVariable Long bandeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "dateDeclaration") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<DeclarationResponse> declarations = declarationService.listerParBande(bandeId, pageable);

        return ResponseEntity.ok(Map.of(
                "content", declarations.getContent(),
                "totalElements", declarations.getTotalElements(),
                "totalPages", declarations.getTotalPages()
        ));
    }

    @GetMapping("/bande/{bandeId}/stats")
    public ResponseEntity<Map<String, Object>> getStats(@PathVariable Long bandeId) {
        DeclarationStatsResponse stats = declarationService.getStatsBande(bandeId);
        return ResponseEntity.ok(Map.of("data", stats));
    }

    @PatchMapping("/{id}/annuler")
    public ResponseEntity<Map<String, Object>> annuler(
            @PathVariable Long id,
            @RequestParam String motif,
            Authentication authentication) {
        String username = authentication != null ? authentication.getName() : "system";
        DeclarationResponse response = declarationService.annulerDeclaration(id, motif, null, username);
        return ResponseEntity.ok(Map.of(
                "data", response,
                "message", "Declaration id=" + id + " annulee avec succes"
        ));
    }

    @GetMapping("/{id}/historique")
    public ResponseEntity<Map<String, Object>> getHistorique(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("content", declarationService.getHistorique(id)));
    }
}
