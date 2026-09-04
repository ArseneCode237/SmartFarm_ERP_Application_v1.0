package com.reseau_partage.vaccination.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.reseau_partage.vaccination.dto.individu.VaccinationIndividuelleRequest;
import com.reseau_partage.vaccination.service.VaccinationIndividuelleService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/vaccination/individus")
public class VaccinationIndividuelleController {
    private final VaccinationIndividuelleService service;
    public VaccinationIndividuelleController(VaccinationIndividuelleService service) { this.service = service; }
    @PostMapping public ResponseEntity<Map<String, Object>> enregistrer(@Valid @RequestBody VaccinationIndividuelleRequest request) { return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("data", service.enregistrer(request), "message", "Vaccination individuelle enregistrée.")); }
    @GetMapping("/animal/{animalId}") public Map<String, Object> historique(@PathVariable Long animalId) { return Map.of("content", service.historique(animalId)); }
    @GetMapping("/animal/{animalId}/statut") public Map<String, Object> statut(@PathVariable Long animalId) { return Map.of("data", service.statut(animalId)); }
    @GetMapping(value = "/animal/{animalId}/certificat", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> certificat(@PathVariable Long animalId) {
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=carnet-vaccinal-" + animalId + ".pdf").body(service.certificat(animalId));
    }
    @GetMapping("/rappels") public Map<String, Object> rappels(@RequestParam Long fermeId, @RequestParam(defaultValue = "30") int horizon) { return Map.of("content", service.rappels(fermeId, horizon)); }
}
