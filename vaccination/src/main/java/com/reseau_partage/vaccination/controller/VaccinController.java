package com.reseau_partage.vaccination.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.reseau_partage.core.entities.Espece;
import com.reseau_partage.vaccination.dto.vaccin.VaccinRequest;
import com.reseau_partage.vaccination.dto.vaccin.VaccinResponse;
import com.reseau_partage.vaccination.service.VaccinService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/vaccination/vaccins")
public class VaccinController {
    private final VaccinService service;

    public VaccinController(VaccinService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> creer(@Valid @RequestBody VaccinRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("data", service.creer(request), "message", "Vaccin créé avec succès."));
    }

    @GetMapping
    public List<VaccinResponse> lister() {
        return service.listerActifs();
    }

    @GetMapping("/espece/{espece}")
    public List<VaccinResponse> listerParEspece(@PathVariable Espece espece) {
        return service.listerParEspece(espece);
    }

    @GetMapping("/{id}")
    public Map<String, Object> get(@PathVariable Long id) {
        return Map.of("data", service.get(id));
    }

    @PutMapping("/{id}")
    public Map<String, Object> modifier(@PathVariable Long id, @Valid @RequestBody VaccinRequest request) {
        return Map.of("data", service.modifier(id, request), "message", "Vaccin modifié avec succès.");
    }

    @PatchMapping("/{id}/desactiver")
    public Map<String, Object> desactiver(@PathVariable Long id) {
        return Map.of("data", service.desactiver(id), "message", "Vaccin désactivé avec succès.");
    }
}
