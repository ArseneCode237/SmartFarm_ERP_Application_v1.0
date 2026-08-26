package com.reseau_partage.stocks.controller;

import java.util.List;
import java.util.Map;

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

import com.reseau_partage.stocks.dto.alerte.AlerteRequest;
import com.reseau_partage.stocks.entities.AlerteStock;
import com.reseau_partage.stocks.service.AlerteStockService;

@RestController
@RequestMapping("/api/stocks/alertes")
public class AlerteController {

    private final AlerteStockService alerteStockService;

    public AlerteController(AlerteStockService alerteStockService) {
        this.alerteStockService = alerteStockService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> lister(
            @RequestParam Long fermeId,
            @RequestParam(required = false) Boolean resolue,
            @RequestParam(required = false) String typeAlerte,
            @RequestParam(required = false) String search) {
        List<AlerteStock> alertes = alerteStockService.listerAlertes(fermeId, resolue, typeAlerte, search);
        return ResponseEntity.ok(Map.of(
                "content", alertes,
                "totalElements", alertes.size(),
                "fermeId", fermeId,
                "message", alertes.size() + " alerte(s) détectée(s)."
        ));
    }

    @GetMapping("/statistiques")
    public ResponseEntity<Map<String, Object>> statistiques(@RequestParam Long fermeId) {
        Map<String, Object> stats = alerteStockService.statistiques(fermeId);
        return ResponseEntity.ok(stats);
    }

    @PatchMapping("/{id}/resoudre")
    public ResponseEntity<AlerteStock> resoudre(
            @PathVariable Long id,
            @RequestBody AlerteRequest request,
            Authentication authentication) {
        Long utilisateurId = extractUtilisateurId(authentication);
        AlerteStock alerte = alerteStockService.resoudre(id, request.getCommentaire(), utilisateurId);
        return ResponseEntity.ok(alerte);
    }

    @PatchMapping("/resoudre-tout")
    public ResponseEntity<Map<String, Object>> resoudreTout(
            @RequestBody AlerteRequest request,
            Authentication authentication) {
        Long utilisateurId = extractUtilisateurId(authentication);
        alerteStockService.resoudreTout(request.getFermeId(), utilisateurId);
        return ResponseEntity.ok(Map.of("message", "Toutes les alertes actives ont été marquées comme résolues.", "fermeId", request.getFermeId()));
    }

    @PostMapping("/synchroniser")
    public ResponseEntity<Map<String, Object>> synchroniser(@RequestParam Long fermeId) {
        alerteStockService.synchroniserToutesAlertes(fermeId);
        return ResponseEntity.ok(Map.of("message", "Synchronisation terminée", "fermeId", fermeId));
    }

    private Long extractUtilisateurId(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) return null;
        // L'authentification JWT stocke l'email comme principal.
        // On tente de résoudre l'ID utilisateur depuis le service d'identification si nécessaire.
        return null;
    }
}
