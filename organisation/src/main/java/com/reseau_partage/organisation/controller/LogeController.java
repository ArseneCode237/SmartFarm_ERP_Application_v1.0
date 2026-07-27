package com.reseau_partage.organisation.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.reseau_partage.organisation.dto.LogeRequest;
import com.reseau_partage.organisation.dto.LogeResponse;
import com.reseau_partage.organisation.service.LogeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/organisation/loges")
public class LogeController {

    private final LogeService service;

    public LogeController(LogeService service) {
        this.service = service;
    }

    /**
     * GET /api/organisation/loges/batiment/{batimentId}
     * Lister toutes les loges d'un bâtiment.
     */
    @GetMapping("/batiment/{batimentId}")
    public ResponseEntity<List<LogeResponse>> listByBatiment(@PathVariable Long batimentId) {
        return ResponseEntity.ok(service.listByBatiment(batimentId));
    }

    /**
     * GET /api/organisation/loges/{id}
     * Récupérer le détail d'une loge.
     */
    @GetMapping("/{id}")
    public ResponseEntity<LogeResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    /**
     * POST /api/organisation/loges
     * Créer une nouvelle loge dans un bâtiment.
     */
    @PostMapping
    public ResponseEntity<LogeResponse> create(@Valid @RequestBody LogeRequest request) {
        LogeResponse response = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * PUT /api/organisation/loges/{id}
     * Modifier une loge existante.
     */
    @PutMapping("/{id}")
    public ResponseEntity<LogeResponse> update(@PathVariable Long id,
                                                @Valid @RequestBody LogeRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    /**
     * DELETE /api/organisation/loges/{id}
     * Supprimer une loge (détache ses animaux et sa bande d'abord).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(Map.of("message", "Loge id=" + id + " supprimée avec succès."));
    }

    /**
     * POST /api/organisation/loges/{id}/affecter-bande
     * Affecter une bande entière à la loge.
     * Body : { "bandeId": 42 }
     */
    @PostMapping("/{id}/affecter-bande")
    public ResponseEntity<LogeResponse> affecterBande(@PathVariable Long id,
                                                      @RequestBody Map<String, Long> body) {
        Long bandeId = body.get("bandeId");
        if (bandeId == null) {
            throw new IllegalArgumentException("Le champ 'bandeId' est obligatoire.");
        }
        return ResponseEntity.ok(service.affecterBande(id, bandeId));
    }

    /**
     * POST /api/organisation/loges/{id}/retirer-bande
     * Retirer la bande actuellement affectée à la loge.
     */
    @PostMapping("/{id}/retirer-bande")
    public ResponseEntity<LogeResponse> retirerBande(@PathVariable Long id) {
        return ResponseEntity.ok(service.retirerBande(id));
    }

    /**
     * POST /api/organisation/loges/{id}/affecter-animaux
     * Affecter des animaux individuels (suivi individuel) à la loge.
     * Body : { "animauxIds": [1, 2, 3] }
     */
    @PostMapping("/{id}/affecter-animaux")
    public ResponseEntity<LogeResponse> affecterAnimaux(@PathVariable Long id,
                                                        @RequestBody Map<String, List<Long>> body) {
        List<Long> animauxIds = body.get("animauxIds");
        if (animauxIds == null) {
            throw new IllegalArgumentException("Le champ 'animauxIds' est obligatoire.");
        }
        return ResponseEntity.ok(service.affecterAnimaux(id, animauxIds));
    }

    /**
     * POST /api/organisation/loges/{id}/retirer-animaux
     * Retirer des animaux de la loge (leur loge devient null).
     * Body : { "animauxIds": [1, 2, 3] }
     */
    @PostMapping("/{id}/retirer-animaux")
    public ResponseEntity<LogeResponse> retirerAnimaux(@PathVariable Long id,
                                                       @RequestBody Map<String, List<Long>> body) {
        List<Long> animauxIds = body.get("animauxIds");
        if (animauxIds == null) {
            throw new IllegalArgumentException("Le champ 'animauxIds' est obligatoire.");
        }
        return ResponseEntity.ok(service.retirerAnimaux(id, animauxIds));
    }

    /**
     * GET /api/organisation/loges/{id}/duplicate
     * Récupère les données d'une loge existante pour pré-remplir un nouveau formulaire.
     * Le code est préfixé avec "COP-" et le nom avec "Copie - " pour indiquer la duplication.
     * La bande et les animaux ne sont PAS renvoyés (ils doivent être réaffectés).
     */
    @GetMapping("/{id}/duplicate")
    public ResponseEntity<LogeRequest> duplicate(@PathVariable Long id) {
        return ResponseEntity.ok(service.duplicate(id));
    }
}
