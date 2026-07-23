package com.reseau_partage.animaux.controller;

import com.reseau_partage.animaux.dto.declaration.DeclarationAnimalRequest;
import com.reseau_partage.animaux.dto.declaration.DeclarationAnimalResponse;
import com.reseau_partage.animaux.service.DeclarationAnimalService;
import com.reseau_partage.core.entities.StatutDeclaration;
import com.reseau_partage.core.entities.TypeDeclaration;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/animaux/declarations/animaux")
public class DeclarationAnimalController {

    private final DeclarationAnimalService declarationAnimalService;

    public DeclarationAnimalController(DeclarationAnimalService declarationAnimalService) {
        this.declarationAnimalService = declarationAnimalService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> creer(@Valid @RequestBody DeclarationAnimalRequest request, Authentication authentication) {
        String username = authentication != null ? authentication.getName() : "system";
        DeclarationAnimalResponse response = declarationAnimalService.creerDeclaration(request, null, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "data", response,
                "message", "Déclaration créée avec succès pour l'animal id = " + request.animalId()
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
        Page<DeclarationAnimalResponse> declarations = declarationAnimalService.listerToutes(fermeId, type, statut, dateDebut, dateFin, pageable);
        return ResponseEntity.ok(Map.of(
                "content", declarations.getContent(),
                "totalElements", declarations.getTotalElements(),
                "totalPages", declarations.getTotalPages()
        ));
    }

    @GetMapping("/{animalId}")
    public ResponseEntity<Map<String, Object>> listerParAnimal(
            @PathVariable Long animalId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "dateDeclaration") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<DeclarationAnimalResponse> declarations = declarationAnimalService.listerParAnimal(animalId, pageable);
        return ResponseEntity.ok(Map.of(
                "content", declarations.getContent(),
                "totalElements", declarations.getTotalElements(),
                "totalPages", declarations.getTotalPages()
        ));
    }

    @PatchMapping("/{id}/annuler")
    public ResponseEntity<Map<String, Object>> annuler(
            @PathVariable Long id,
            @RequestParam String motif,
            Authentication authentication) {
        String username = authentication != null ? authentication.getName() : "system";
        DeclarationAnimalResponse response = declarationAnimalService.annulerDeclaration(id, motif, null, username);
        return ResponseEntity.ok(Map.of(
                "data", response,
                "message", "Déclaration id = " + id + " annulée avec succès"
        ));
    }

    @GetMapping("/{id}/historique")
    public ResponseEntity<Map<String, Object>> getHistorique(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("content", declarationAnimalService.getHistorique(id)));
    }
}
