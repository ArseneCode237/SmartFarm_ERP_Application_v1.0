package com.reseau_partage.animaux.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.reseau_partage.animaux.dto.bovin.BilanLactationResponse;
import com.reseau_partage.animaux.dto.bovin.ControleLaitierRequest;
import com.reseau_partage.animaux.dto.bovin.ControleLaitierResponse;
import com.reseau_partage.animaux.service.ControleLaitierService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/animaux/bovins/lait")
public class ControleLaitierController {

    private final ControleLaitierService service;

    public ControleLaitierController(ControleLaitierService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody ControleLaitierRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("data", service.create(request)));
    }

    @PostMapping("/batch")
    public ResponseEntity<Map<String, Object>> createBatch(
            @Valid @RequestBody List<@Valid ControleLaitierRequest> requests) {
        List<ControleLaitierResponse> result = service.createBatch(requests);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("content", result, "totalElements", result.size()));
    }

    @GetMapping("/animal/{id}")
    public ResponseEntity<Map<String, Object>> historique(@PathVariable Long id) {
        List<ControleLaitierResponse> result = service.historique(id);
        return ResponseEntity.ok(Map.of("content", result, "totalElements", result.size()));
    }

    @GetMapping("/animal/{id}/courbe")
    public ResponseEntity<Map<String, Object>> courbe(@PathVariable Long id) {
        return historique(id);
    }

    @GetMapping("/animal/{id}/bilan/{lactation}")
    public ResponseEntity<Map<String, Object>> bilan(@PathVariable Long id, @PathVariable Integer lactation) {
        BilanLactationResponse result = service.bilan(id, lactation);
        return ResponseEntity.ok(Map.of("data", result));
    }

    @GetMapping("/sous-performeuses")
    public ResponseEntity<Map<String, Object>> sousPerformeuses(@RequestParam Long fermeId) {
        List<ControleLaitierResponse> result = service.sousPerformeuses(fermeId);
        return ResponseEntity.ok(Map.of("content", result, "totalElements", result.size()));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> stats(
            @RequestParam Long fermeId, @RequestParam LocalDate debut, @RequestParam LocalDate fin) {
        BigDecimal production = service.productionFerme(fermeId, debut, fin);
        return ResponseEntity.ok(Map.of("fermeId", fermeId, "debut", debut, "fin", fin,
                "productionTotaleKg", production));
    }
}
