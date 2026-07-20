package com.reseau_partage.animaux.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.reseau_partage.animaux.dto.porcin.PorcinRequest;
import com.reseau_partage.animaux.dto.porcin.PorcinResponse;
import com.reseau_partage.animaux.dto.porcin.PorcinUpdateRequest;
import com.reseau_partage.animaux.service.PorcinService;
import com.reseau_partage.core.entities.StatutReproductifPorcin;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/animaux/porcs")
public class PorcinController {

    private final PorcinService service;

    public PorcinController(PorcinService service) {
        this.service = service;
    }

    /**
     * POST /api/animaux/porcs
     * Créer un porcin individuel (achat, naissance interne, don).
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody PorcinRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("data", service.create(request)));
    }

    /**
     * GET /api/animaux/porcs
     * Lister les porcins actifs avec filtres optionnels.
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(required = false) StatutReproductifPorcin statutReproductif,
            @RequestParam(required = false) Long fermeId) {
        List<PorcinResponse> result = service.list(statutReproductif, fermeId);
        return ResponseEntity.ok(Map.of("content", result, "totalElements", result.size()));
    }

    /**
     * GET /api/animaux/porcs/{id}
     * Fiche complète d'un porcin (profil reproductif + historique carrière).
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> get(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("data", service.get(id)));
    }

    /**
     * PUT /api/animaux/porcs/{id}
     * Mettre à jour les informations d'un porc déjà créé.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(
            @PathVariable Long id,
            @Valid @RequestBody PorcinUpdateRequest request) {
        return ResponseEntity.ok(Map.of("data", service.update(id, request)));
    }

    /**
     * PATCH /api/animaux/porcs/{id}/statut
     * Changer le statut reproductif selon la matrice de transitions autorisées.
     * Corps : { "statut": "EN_ATTENTE_SAILLIE" }
     */
    @PatchMapping("/{id}/statut")
    public ResponseEntity<Map<String, Object>> changerStatut(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        StatutReproductifPorcin nouveau;
        try {
            nouveau = StatutReproductifPorcin.valueOf(body.get("statut").trim().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException(
                    "Statut invalide : " + body.get("statut") +
                    ". Valeurs acceptées : " + java.util.Arrays.toString(StatutReproductifPorcin.values()));
        }
        return ResponseEntity.ok(Map.of("data", service.changerStatut(id, nouveau)));
    }

    /**
     * POST /api/animaux/porcs/{id}/transfert
     * Transférer un porcin vers une autre structure.
     * Corps : { "structureDestinationId": 5, "motif": "Transfert maternité" }
     */
    @PostMapping("/{id}/transfert")
    public ResponseEntity<Map<String, Object>> transferer(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        Long structureDestId = Long.parseLong(body.get("structureDestinationId").toString());
        String motif = (String) body.get("motif");
        return ResponseEntity.ok(Map.of("data", service.transferer(id, structureDestId, motif)));
    }

    /**
     * POST /api/animaux/porcs/{id}/reformer
     * Réformer définitivement un animal.
     * Corps : { "motif": "5ème portée atteinte" }
     */
    @PostMapping("/{id}/reformer")
    public ResponseEntity<Void> reformer(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        String motif = body != null ? body.get("motif") : null;
        service.reformer(id, motif);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/animaux/porcs/{id}/carriere
     * Historique reproductif complet d'une truie :
     * toutes ses saillies + toutes ses portées + statistiques carrière.
     */
    @GetMapping("/{id}/carriere")
    public ResponseEntity<Map<String, Object>> carriere(@PathVariable Long id) {
        return ResponseEntity.ok(service.carriere(id));
    }

    /**
     * GET /api/animaux/porcs/dashboard
     * KPIs reproduction par ferme.
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> dashboard(@RequestParam Long fermeId) {
        return ResponseEntity.ok(service.dashboard(fermeId));
    }
}
