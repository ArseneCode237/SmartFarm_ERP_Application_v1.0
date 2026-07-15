package com.reseau_partage.organisation.controller;

import com.reseau_partage.core.entities.StatutSite;
import com.reseau_partage.organisation.dto.SiteRequest;
import com.reseau_partage.organisation.dto.StatutRequest;
import com.reseau_partage.organisation.service.OrganisationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/organisation/sites")
public class SiteController {

    private final OrganisationService service;

    public SiteController(OrganisationService service) {
        this.service = service;
    }

    /**
     * POST /api/organisation/sites
     * Créer un nouveau site.
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody SiteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createSite(request));
    }

    /**
     * GET /api/organisation/sites/ferme/{fermeId}
     * Lister tous les sites d'une ferme.
     */
    @GetMapping("/ferme/{fermeId}")
    public ResponseEntity<List<Map<String, Object>>> listByFerme(@PathVariable Long fermeId) {
        return ResponseEntity.ok(service.listSites(fermeId));
    }

    /**
     * GET /api/organisation/sites/{id}
     * Détail d'un site avec compteur de structures.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.getSite(id));
    }

    /**
     * PUT /api/organisation/sites/{id}
     * Modifier un site.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id,
            @Valid @RequestBody SiteRequest request) {
        return ResponseEntity.ok(service.updateSite(id, request));
    }

    /**
     * PATCH /api/organisation/sites/{id}/statut
     * Changer le statut d'un site (ACTIF, INACTIF, ARCHIVE).
     * Un passage à ARCHIVE cascade vers toutes les structures du site.
     *
     * Corps attendu : { "statut": "INACTIF" }
     */
    @PatchMapping("/{id}/statut")
    public ResponseEntity<Map<String, Object>> changeStatut(@PathVariable Long id,
            @Valid @RequestBody StatutRequest request) {
        StatutSite statut;
        try {
            statut = StatutSite.valueOf(request.statut().trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Statut invalide : " + request.statut() + ". Valeurs acceptées : ACTIF, INACTIF, ARCHIVE.");
        }
        return ResponseEntity.ok(service.siteStatus(id, statut));
    }

    /**
     * GET /api/organisation/sites/{id}/statistiques
     * Statistiques agrégées du site (structures par type, animaux, employés).
     */
    @GetMapping("/{id}/statistiques")
    public ResponseEntity<Map<String, Object>> stats(@PathVariable Long id) {
        return ResponseEntity.ok(service.siteStats(id));
    }
}
