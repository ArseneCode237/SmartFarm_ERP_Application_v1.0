package com.reseau_partage.animaux.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.reseau_partage.animaux.dto.porcin.ExtractionBandeRequest;
import com.reseau_partage.animaux.dto.porcin.ExtractionBandeResponse;
import com.reseau_partage.animaux.service.ExtractionBandeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/animaux/porcs/extractions")
public class ExtractionBandeController {

    private final ExtractionBandeService service;

    public ExtractionBandeController(ExtractionBandeService service) {
        this.service = service;
    }

    /**
     * POST /api/animaux/porcs/extractions
     * Extraire des animaux d'une bande porcine pour les passer en suivi individuel.
     * Chaque animal extrait reçoit une fiche individuelle et un ProfilPorcin.
     *
     * Corps :
     * {
     *   "bandeId": 1,
     *   "dateExtraction": "2026-07-18",
     *   "structureDestinationId": 5,
     *   "animaux": [
     *     { "sexe": "FEMELLE", "poidsKg": 65.0, "codeBoucle": "FR001" },
     *     { "sexe": "MALE",    "poidsKg": 72.5 }
     *   ]
     * }
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> extraire(
            @Valid @RequestBody ExtractionBandeRequest request) {
        ExtractionBandeResponse data = service.extraire(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("data", data, "message", data.nbExtraits() + " animaux extraits de la bande id=" + request.bandeId() + " et passés en suivi individuel."));
    }
}
