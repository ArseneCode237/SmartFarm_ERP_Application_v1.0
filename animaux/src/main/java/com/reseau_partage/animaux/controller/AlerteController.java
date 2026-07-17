package com.reseau_partage.animaux.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.reseau_partage.animaux.dto.alerte.AlerteResponse;
import com.reseau_partage.animaux.service.AlerteService;

@RestController
@RequestMapping("/api/animaux/alertes")
public class AlerteController {

    private final AlerteService service;

    public AlerteController(AlerteService service) {
        this.service = service;
    }

    @GetMapping("/ferme/{fermeId}")
    public ResponseEntity<Map<String, Object>> alertesFerme(@PathVariable Long fermeId,
                                                            @RequestParam(defaultValue = "30") int jours) {
        List<AlerteResponse> alertes = service.alertesFerme(fermeId, jours);
        return ResponseEntity.ok(Map.of("content", alertes, "totalElements", alertes.size()));
    }
}
