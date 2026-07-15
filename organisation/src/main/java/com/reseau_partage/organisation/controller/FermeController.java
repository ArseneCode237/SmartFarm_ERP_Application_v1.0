package com.reseau_partage.organisation.controller;

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
import org.springframework.web.bind.annotation.RestController;

import com.reseau_partage.organisation.dto.FermeRequest;
import com.reseau_partage.organisation.service.OrganisationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/organisation/fermes")
public class FermeController {

    private final OrganisationService service;

    public FermeController(OrganisationService service) {
        this.service = service;
    }

    /**
     * POST /api/organisation/fermes
     * Créer une nouvelle ferme.
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody FermeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createFerme(request));
    }

    /**
     * GET /api/organisation/fermes
     * Lister toutes les fermes actives.
     */
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> list() {
        return ResponseEntity.ok(service.listFermes());
    }

    /**
     * GET /api/organisation/fermes/{id}
     * Détail d'une ferme avec compteurs.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.getFerme(id));
    }

    /**
     * PUT /api/organisation/fermes/{id}
     * Modifier une ferme.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id,
            @Valid @RequestBody FermeRequest request) {
        return ResponseEntity.ok(service.updateFerme(id, request));
    }

    /**
     * PATCH /api/organisation/fermes/{id}/archiver
     * Archiver une ferme (ADMIN uniquement — sécurisé dans SecurityConfig).
     * Cascade : tous ses sites et structures passent en ARCHIVE.
     */
    @PatchMapping("/{id}/archiver")
    public ResponseEntity<Void> archive(@PathVariable Long id) {
        service.archiveFerme(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/organisation/fermes/{id}/statistiques
     * Statistiques agrégées d'une ferme (nombre de sites, structures).
     */
    @GetMapping("/{id}/statistiques")
    public ResponseEntity<Map<String, Object>> stats(@PathVariable Long id) {
        return ResponseEntity.ok(service.fermeStats(id));
    }
}
