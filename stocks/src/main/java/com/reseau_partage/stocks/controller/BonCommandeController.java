package com.reseau_partage.stocks.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.reseau_partage.stocks.dto.boncommande.BonCommandeRequest;
import com.reseau_partage.stocks.dto.boncommande.BonCommandeResponse;
import com.reseau_partage.stocks.dto.boncommande.LigneBonCommandeRequest;
import com.reseau_partage.stocks.service.BonCommandeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/stocks/bons-commande")
public class BonCommandeController {

    private final BonCommandeService bonCommandeService;

    public BonCommandeController(BonCommandeService bonCommandeService) {
        this.bonCommandeService = bonCommandeService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody BonCommandeRequest request, Authentication authentication) {
        BonCommandeResponse response = bonCommandeService.creerBonCommande(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("data", response, "message", "Bon de commande cree avec succes. numeroBc=" + response.getNumeroBc()));
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(required = false) Long fermeId,
            @RequestParam(required = false) com.reseau_partage.core.entities.enumtypes.StatutBonCommande statut,
            org.springframework.data.domain.Pageable pageable) {
        var page = bonCommandeService.listerBons(fermeId, statut, pageable);
        return ResponseEntity.ok(Map.of("content", page.getContent(), "totalElements", page.getTotalElements(), "totalPages", page.getTotalPages()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> get(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("data", bonCommandeService.getById(id)));
    }

    @PatchMapping("/{id}/envoyer")
    public ResponseEntity<Map<String, Object>> envoyer(@PathVariable Long id) {
        BonCommandeResponse response = bonCommandeService.envoyerBonCommande(id);
        return ResponseEntity.ok(Map.of("data", response, "message", "Bon de commande id=" + id + " envoyer."));
    }

    @PatchMapping("/{id}/confirmer")
    public ResponseEntity<Map<String, Object>> confirmer(@PathVariable Long id) {
        BonCommandeResponse response = bonCommandeService.confirmerBonCommande(id);
        return ResponseEntity.ok(Map.of("data", response, "message", "Bon de commande id=" + id + " confirme."));
    }

    @PostMapping("/{id}/reception")
    public ResponseEntity<Map<String, Object>> receptionner(@PathVariable Long id, @RequestBody List<LigneBonCommandeRequest> lignesRecues) {
        BonCommandeResponse response = bonCommandeService.receptionnerBonCommande(id, lignesRecues);
        return ResponseEntity.ok(Map.of("data", response, "message", "Bon de commande id=" + id + " receptionne."));
    }

    @PatchMapping("/{id}/annuler")
    public ResponseEntity<Map<String, Object>> annuler(@PathVariable Long id) {
        BonCommandeResponse response = bonCommandeService.annulerBonCommande(id);
        return ResponseEntity.ok(Map.of("data", response, "message", "Bon de commande id=" + id + " annule."));
    }
}