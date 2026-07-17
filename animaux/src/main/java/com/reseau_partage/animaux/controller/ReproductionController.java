package com.reseau_partage.animaux.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.reseau_partage.animaux.dto.reproduction.EvenementReproductionRequest;
import com.reseau_partage.animaux.dto.reproduction.EvenementReproductionResponse;
import com.reseau_partage.animaux.dto.reproduction.MiseBasRequest;
import com.reseau_partage.animaux.dto.reproduction.SevrageRequest;
import com.reseau_partage.animaux.service.ReproductionService;

@RestController
@RequestMapping("/api/animaux/reproduction")
public class ReproductionController {

    private final ReproductionService service;

    public ReproductionController(ReproductionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> enregistrerSaillie(@RequestBody EvenementReproductionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("data", service.enregistrerSaillie(request)));
    }

    @GetMapping("/animal/{id}")
    public ResponseEntity<Map<String, Object>> historique(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("content", service.historiqueAnimal(id)));
    }

    @PostMapping("/{id}/mise-bas")
    public ResponseEntity<Map<String, Object>> declarerMiseBas(@PathVariable Long id, @RequestBody MiseBasRequest request) {
        return ResponseEntity.ok(Map.of("data", service.declarerMiseBas(id, request)));
    }

    @PutMapping("/{id}/sevrage")
    public ResponseEntity<Map<String, Object>> enregistrerSevrage(@PathVariable Long id, @RequestBody SevrageRequest request) {
        return ResponseEntity.ok(Map.of("data", service.enregistrerSevrage(id, request)));
    }

    @GetMapping("/alertes")
    public ResponseEntity<Map<String, Object>> alertes(@RequestParam(defaultValue = "30") int jours) {
        return ResponseEntity.ok(Map.of("content", service.alertes(jours)));
    }

    @PatchMapping("/{id}/statut-reproducteur")
    public ResponseEntity<Void> changerStatut(@PathVariable Long id, @RequestBody Map<String, String> body) {
        service.changerStatutReproducteur(id, body.get("statut"));
        return ResponseEntity.noContent().build();
    }
}
