package com.reseau_partage.animaux.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.reseau_partage.animaux.dto.bovin.BovinMotifRequest;
import com.reseau_partage.animaux.dto.bovin.SaillieBovinConfirmationRequest;
import com.reseau_partage.animaux.dto.bovin.SaillieBovinRequest;
import com.reseau_partage.animaux.dto.bovin.SaillieBovinResponse;
import com.reseau_partage.animaux.service.SaillieBovinService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/animaux/bovins/saillies")
public class SaillieBovinController {

    private final SaillieBovinService service;

    public SaillieBovinController(SaillieBovinService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody SaillieBovinRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("data", service.create(request)));
    }

    @GetMapping("/vache/{id}")
    public ResponseEntity<Map<String, Object>> historique(@PathVariable Long id) {
        List<SaillieBovinResponse> result = service.historique(id);
        return ResponseEntity.ok(Map.of("content", result, "totalElements", result.size()));
    }

    @GetMapping("/echo-en-retard")
    public ResponseEntity<Map<String, Object>> echoesEnRetard() {
        List<SaillieBovinResponse> result = service.echoesEnRetard();
        return ResponseEntity.ok(Map.of("content", result, "totalElements", result.size()));
    }

    @PatchMapping("/{id}/confirmation")
    public ResponseEntity<Map<String, Object>> confirmer(
            @PathVariable Long id, @Valid @RequestBody SaillieBovinConfirmationRequest request) {
        return ResponseEntity.ok(Map.of("data", service.confirmer(id, request)));
    }

    @PatchMapping("/{id}/avortement")
    public ResponseEntity<Map<String, Object>> avorter(
            @PathVariable Long id, @Valid @RequestBody BovinMotifRequest request) {
        return ResponseEntity.ok(Map.of("data", service.avorter(id, request.motif())));
    }
}
