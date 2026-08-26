package com.reseau_partage.stocks.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.reseau_partage.stocks.dto.fournisseur.FournisseurRequest;
import com.reseau_partage.stocks.dto.fournisseur.FournisseurResponse;
import com.reseau_partage.stocks.service.FournisseurService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/stocks/fournisseurs")
public class FournisseurController {

    private final FournisseurService fournisseurService;

    public FournisseurController(FournisseurService fournisseurService) {
        this.fournisseurService = fournisseurService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody FournisseurRequest request, @RequestParam Long fermeId, Authentication authentication) {
        FournisseurResponse response = fournisseurService.creerFournisseur(request, fermeId);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("data", response, "message", "Fournisseur cree avec succes."));
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> list(@RequestParam Long fermeId) {
        return ResponseEntity.ok(Map.of("content", fournisseurService.listerFournisseurs(fermeId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> get(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("data", fournisseurService.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @Valid @RequestBody FournisseurRequest request) {
        FournisseurResponse response = fournisseurService.mettreAJourFournisseur(id, request);
        return ResponseEntity.ok(Map.of("data", response, "message", "Fournisseur id=" + id + " mis a jour avec succes."));
    }

    @GetMapping("/{id}/articles")
    public ResponseEntity<Map<String, Object>> articles(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("content", fournisseurService.getArticlesFournisseur(id)));
    }
}