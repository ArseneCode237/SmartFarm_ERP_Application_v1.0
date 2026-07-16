package com.reseau_partage.organisation.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.reseau_partage.core.entities.StatutFerme;
import com.reseau_partage.organisation.dto.FermeRequest;
import com.reseau_partage.organisation.dto.StatutRequest;
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
     * L'utilisateur connecté devient automatiquement le propriétaire.
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody FermeRequest request,
                                                      Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createFerme(request, authentication.getName()));
    }

    /**
     * GET /api/organisation/fermes
     * Lister toutes les fermes du système (non archivées).
     */
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> list() {
        return ResponseEntity.ok(service.listFermes());
    }

    /**
     * GET /api/organisation/fermes/mes-fermes
     * Lister uniquement les fermes appartenant à l'utilisateur connecté.
     */
    @GetMapping("/mes-fermes")
    public ResponseEntity<List<Map<String, Object>>> mesFermes(Authentication authentication) {
        return ResponseEntity.ok(service.listMesFermes(authentication.getName()));
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
     * Archiver une ferme (ADMIN uniquement).
     * Cascade : tous ses sites et structures passent en ARCHIVE.
     */
    @PatchMapping("/{id}/archiver")
    public ResponseEntity<Void> archive(@PathVariable Long id) {
        service.archiveFerme(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * PATCH /api/organisation/fermes/{id}/statut
     * Changer le statut opérationnel : ACTIF, INACTIF, MAINTENANCE.
     *
     * Corps attendu : { "statut": "MAINTENANCE" }
     */
    @PatchMapping("/{id}/statut")
    public ResponseEntity<Map<String, Object>> changeStatut(@PathVariable Long id,
                                                            @Valid @RequestBody StatutRequest request) {
        StatutFerme statut;
        try {
            statut = StatutFerme.valueOf(request.statut().trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Statut invalide : " + request.statut() +
                    ". Valeurs acceptées : ACTIF, INACTIF, MAINTENANCE.");
        }
        return ResponseEntity.ok(service.changeFermeStatut(id, statut));
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
