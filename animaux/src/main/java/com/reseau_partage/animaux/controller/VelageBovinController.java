package com.reseau_partage.animaux.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.reseau_partage.animaux.dto.bovin.VelageBovinRequest;
import com.reseau_partage.animaux.dto.bovin.VelageBovinResponse;
import com.reseau_partage.animaux.service.VelageBovinService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/animaux/bovins/velages")
public class VelageBovinController {

    private final VelageBovinService service;

    public VelageBovinController(VelageBovinService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody VelageBovinRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("data", service.create(request)));
    }

    @GetMapping("/vache/{id}")
    public ResponseEntity<Map<String, Object>> historique(@PathVariable Long id) {
        List<VelageBovinResponse> result = service.historique(id);
        return ResponseEntity.ok(Map.of("content", result, "totalElements", result.size()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> get(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("data", service.get(id)));
    }
}
