package com.reseau_partage.animaux.controller;

import com.reseau_partage.animaux.dto.saillie.SaillieConfirmationRequest;
import com.reseau_partage.animaux.dto.saillie.SaillieRequest;
import com.reseau_partage.animaux.service.SaillieService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/animaux/porcs/saillies")
public class SaillieController {

    private final SaillieService service;

    public SaillieController(SaillieService service) {
        this.service = service;
    }

    /**
     * POST /api/animaux/porcs/saillies
     * Enregistrer une saillie (naturelle ou IA).
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> enregistrer(
            @Valid @RequestBody SaillieRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("data", service.enregistrer(request)));
    }

    /**
     * GET /api/animaux/porcs/saillies/truie/{id}
     * Historique des saillies d'une truie.
     */
    @GetMapping("/truie/{id}")
    public ResponseEntity<Map<String, Object>> parTruie(@PathVariable Long id) {
        var result = service.historiqueParTruie(id);
        return ResponseEntity.ok(Map.of("content", result, "totalElements", result.size()));
    }

    /**
     * GET /api/animaux/porcs/saillies/verrat/{id}
     * Historique et taux de fertilité d'un verrat.
     */
    @GetMapping("/verrat/{id}")
    public ResponseEntity<Map<String, Object>> statsVerrat(@PathVariable Long id) {
        return ResponseEntity.ok(service.statsVerrat(id));
    }

    /**
     * PATCH /api/animaux/porcs/saillies/{id}/confirmation
     * Confirmer ou infirmer la gestation après échographie à J+28.
     * Corps : { "statut": "CONFIRMEE", "dateEcho": "2026-08-15" }
     */
    @PatchMapping("/{id}/confirmation")
    public ResponseEntity<Map<String, Object>> confirmer(
            @PathVariable Long id,
            @Valid @RequestBody SaillieConfirmationRequest request) {
        return ResponseEntity.ok(Map.of("data", service.confirmer(id, request)));
    }

    /**
     * PATCH /api/animaux/porcs/saillies/{id}/avortement
     * Déclarer un avortement en cours de gestation.
     * Corps optionnel : { "notes": "..." }
     */
    @PatchMapping("/{id}/avortement")
    public ResponseEntity<Map<String, Object>> avortement(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        String notes = body != null ? body.get("notes") : null;
        return ResponseEntity.ok(Map.of("data", service.avortement(id, notes)));
    }

    /**
     * GET /api/animaux/porcs/saillies/alertes/echo-attendu
     * Saillies EN_ATTENTE depuis plus de 28 jours — écho à effectuer.
     */
    @GetMapping("/alertes/echo-attendu")
    public ResponseEntity<Map<String, Object>> alertesEcho() {
        var result = service.alertesEchoAttendu();
        return ResponseEntity.ok(Map.of("content", result, "totalElements", result.size()));
    }
}
