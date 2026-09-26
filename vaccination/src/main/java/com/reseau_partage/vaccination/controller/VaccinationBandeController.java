package com.reseau_partage.vaccination.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.reseau_partage.vaccination.dto.bande.VaccinationBandeRequest;
import com.reseau_partage.vaccination.service.VaccinationBandeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/vaccination/bandes")
public class VaccinationBandeController {
    private final VaccinationBandeService service;
    public VaccinationBandeController(VaccinationBandeService service) { this.service = service; }
    @PostMapping public ResponseEntity<Map<String, Object>> enregistrer(@Valid @RequestBody VaccinationBandeRequest request) { return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("data", service.enregistrer(request), "message", "Vaccination de bande enregistrée.")); }
    @GetMapping("/bande/{bandeId}") public Map<String, Object> historique(@PathVariable Long bandeId) { return Map.of("content", service.historique(bandeId)); }
    @GetMapping("/bande/{bandeId}/statut") public Map<String, Object> statut(@PathVariable Long bandeId) { return Map.of("data", service.statut(bandeId)); }
    @GetMapping("/bande/{bandeId}/delai-attente") public Map<String, Object> delai(@PathVariable Long bandeId) { return Map.of("content", service.delaiAttente(bandeId)); }
    @GetMapping("/rappels") public Map<String, Object> rappels(@RequestParam Long fermeId, @RequestParam(defaultValue = "30") int horizon) { return Map.of("content", service.rappels(fermeId, horizon)); }
    @GetMapping("/planifiees") public Map<String, Object> planifiees(@RequestParam Long fermeId) { return Map.of("content", service.planifiees(fermeId)); }
    @GetMapping("/ferme/{fermeId}") public Map<String, Object> toutesParFerme(@PathVariable Long fermeId) { return Map.of("content", service.toutesParFerme(fermeId)); }
    @PostMapping("/plan/{planId}/bande/{bandeId}") public Map<String, Object> appliquer(@PathVariable Long planId, @PathVariable Long bandeId) { return Map.of("content", service.appliquerPlan(bandeId, planId)); }
    @PatchMapping("/{id}/annuler") public Map<String, Object> annuler(@PathVariable Long id) { return Map.of("data", service.annuler(id), "message", "Vaccination planifiée annulée."); }
}
