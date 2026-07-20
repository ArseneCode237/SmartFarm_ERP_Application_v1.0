package com.reseau_partage.animaux.controller;

import com.reseau_partage.animaux.dto.misebas.MiseBaRequest;
import com.reseau_partage.animaux.dto.misebas.MiseBaResponse;
import com.reseau_partage.animaux.dto.misebas.SevrageRequest;
import com.reseau_partage.animaux.service.MiseBaService;
import com.reseau_partage.animaux.service.PorcinService;
import com.reseau_partage.core.entities.ProfilPorcin;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/animaux/porcs/mises-bas")
public class MiseBaController {

    private final MiseBaService service;
    private final PorcinService porcinService;

    public MiseBaController(MiseBaService service, PorcinService porcinService) {
        this.service       = service;
        this.porcinService = porcinService;
    }

    /**
     * POST /api/animaux/porcs/mises-bas
     * Déclarer une mise-bas.
     * Crée automatiquement les porcelets vivants et met à jour le profil de la truie.
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> declarer(
            @Valid @RequestBody MiseBaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("data", service.declarer(request)));
    }

    /**
     * GET /api/animaux/porcs/mises-bas/truie/{id}
     * Toutes les portées d'une truie (historique reproductif).
     */
    @GetMapping("/truie/{id}")
    public ResponseEntity<Map<String, Object>> parTruie(@PathVariable Long id) {
        List<MiseBaResponse> result = service.parTruie(id);
        return ResponseEntity.ok(Map.of("content", result, "totalElements", result.size()));
    }

    /**
     * GET /api/animaux/porcs/mises-bas/{id}
     * Détail d'une mise-bas.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> get(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("data", service.get(id)));
    }

    /**
     * PATCH /api/animaux/porcs/mises-bas/{id}/sevrage
     * Enregistrer le sevrage d'une portée.
     * Fait passer la truie au statut EN_CHALEUR et recalcule les moyennes de carrière.
     */
    @PatchMapping("/{id}/sevrage")
    public ResponseEntity<Map<String, Object>> sevrer(
            @PathVariable Long id,
            @Valid @RequestBody SevrageRequest request) {
        return ResponseEntity.ok(Map.of("data", service.sevrer(id, request)));
    }

    /**
     * GET /api/animaux/porcs/mises-bas/alertes/imminentes
     * Truies en gestation dont la mise-bas est prévue dans les 7 prochains jours.
     */
    @GetMapping("/alertes/imminentes")
    public ResponseEntity<Map<String, Object>> alertesImminentes() {
        List<ProfilPorcin> profils = service.alertesImminentes();
        List<Map<String, Object>> result = profils.stream().map(p -> Map.<String, Object>of(
                "truieId",          p.getAnimal().getId(),
                "truieCode",        p.getAnimal().getCodeUnique(),
                "dateMiseBasPrevue", p.getDateMiseBasPrevue(),
                "numeroPorteePrevue", p.getNbPorteesTotal() + 1,
                "fiche",            porcinService.toResponse(p.getAnimal())
        )).toList();
        return ResponseEntity.ok(Map.of("content", result, "totalElements", result.size()));
    }

    /**
     * GET /api/animaux/porcs/mises-bas/alertes/sevrages
     * Portées dont le sevrage est à effectuer cette semaine.
     */
    @GetMapping("/alertes/sevrages")
    public ResponseEntity<Map<String, Object>> alertesSevrages() {
        List<MiseBaResponse> result = service.alertesSevrages();
        return ResponseEntity.ok(Map.of("content", result, "totalElements", result.size()));
    }
}
