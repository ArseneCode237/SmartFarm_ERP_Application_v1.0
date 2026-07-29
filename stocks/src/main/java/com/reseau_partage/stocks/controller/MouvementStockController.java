package com.reseau_partage.stocks.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.reseau_partage.stocks.dto.mouvement.MouvementRequest;
import com.reseau_partage.stocks.dto.mouvement.MouvementResponse;
import com.reseau_partage.stocks.service.MouvementStockService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/stocks/mouvements")
public class MouvementStockController {

    private final MouvementStockService mouvementStockService;

    public MouvementStockController(MouvementStockService mouvementStockService) {
        this.mouvementStockService = mouvementStockService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> enregistrer(@Valid @RequestBody MouvementRequest request, Authentication authentication) {
        MouvementResponse response = mouvementStockService.enregistrerMouvement(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("data", response, "message", "Mouvement enregistre avec succes."));
    }

    @GetMapping("/article/{id}")
    public ResponseEntity<Map<String, Object>> historique(@PathVariable Long id, org.springframework.data.domain.Pageable pageable) {
        var page = mouvementStockService.historiqueArticle(id, pageable);
        return ResponseEntity.ok(Map.of("content", page.getContent(), "totalElements", page.getTotalElements(), "totalPages", page.getTotalPages()));
    }

    @GetMapping("/ferme/{fermeId}")
    public ResponseEntity<Map<String, Object>> parFerme(@PathVariable Long fermeId,
            @RequestParam(required = false) LocalDate debut,
            @RequestParam(required = false) LocalDate fin,
            org.springframework.data.domain.Pageable pageable) {
        var page = mouvementStockService.mouvementsParFerme(fermeId, debut, fin, pageable);
        return ResponseEntity.ok(Map.of("content", page.getContent(), "totalElements", page.getTotalElements(), "totalPages", page.getTotalPages()));
    }

    @GetMapping("/bande/{bandeId}")
    public ResponseEntity<Map<String, Object>> parBande(@PathVariable Long bandeId) {
        return ResponseEntity.ok(Map.of("content", mouvementStockService.mouvementsParBande(bandeId)));
    }

    @GetMapping("/vaccination/{vaccinationId}")
    public ResponseEntity<Map<String, Object>> parVaccination(@PathVariable Long vaccinationId) {
        return ResponseEntity.ok(Map.of("content", mouvementStockService.mouvementsParVaccination(vaccinationId)));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> stats(@RequestParam Long fermeId,
            @RequestParam LocalDate debut,
            @RequestParam LocalDate fin) {
        BigDecimal depenses = mouvementStockService.depensesAchatsSurPeriode(fermeId, debut, fin);
        return ResponseEntity.ok(Map.of("depensesAchatsTotal", depenses));
    }
}