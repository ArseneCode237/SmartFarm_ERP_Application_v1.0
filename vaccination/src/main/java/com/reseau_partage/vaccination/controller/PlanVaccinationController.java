package com.reseau_partage.vaccination.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.reseau_partage.vaccination.dto.plan.PlanVaccinationRequest;
import com.reseau_partage.vaccination.dto.plan.PlanVaccinationResponse;
import com.reseau_partage.vaccination.service.VaccinationBandeService;
import com.reseau_partage.vaccination.service.PlanVaccinationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/vaccination/plans")
public class PlanVaccinationController {
    private final PlanVaccinationService service;
    private final VaccinationBandeService bandeService;

    public PlanVaccinationController(PlanVaccinationService service, VaccinationBandeService bandeService) {
        this.service = service;
        this.bandeService = bandeService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> creer(@Valid @RequestBody PlanVaccinationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("data", service.creer(request), "message", "Plan de vaccination créé avec succès."));
    }

    @GetMapping("/ferme/{fermeId}")
    public List<PlanVaccinationResponse> lister(@PathVariable Long fermeId) {
        return service.lister(fermeId);
    }

    @GetMapping("/{id}")
    public Map<String, Object> get(@PathVariable Long id) {
        return Map.of("data", service.get(id));
    }

    @PutMapping("/{id}")
    public Map<String, Object> modifier(@PathVariable Long id, @Valid @RequestBody PlanVaccinationRequest request) {
        return Map.of("data", service.modifier(id, request), "message", "Plan de vaccination modifié avec succès.");
    }

    @PostMapping("/{planId}/appliquer/bande/{bandeId}")
    public Map<String, Object> appliquer(@PathVariable Long planId, @PathVariable Long bandeId) {
        return Map.of("content", bandeService.appliquerPlan(bandeId, planId));
    }
}
