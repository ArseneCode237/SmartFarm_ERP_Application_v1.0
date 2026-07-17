package com.reseau_partage.animaux.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.reseau_partage.animaux.dto.config.ConfigEspeceRequest;
import com.reseau_partage.animaux.dto.config.ConfigEspeceResponse;
import com.reseau_partage.animaux.dto.config.CourbeCroissanceRequest;
import com.reseau_partage.animaux.dto.config.TypeEvenementCustomRequest;
import com.reseau_partage.animaux.service.ConfigEspeceService;
import com.reseau_partage.core.entities.CourbeCroissanceReference;
import com.reseau_partage.core.entities.Espece;
import com.reseau_partage.core.entities.TypeEvenementCustom;

@RestController
@RequestMapping("/api/animaux/config")
public class ConfigEspeceController {

    private final ConfigEspeceService service;

    public ConfigEspeceController(ConfigEspeceService service) {
        this.service = service;
    }

    @GetMapping("/especes")
    public ResponseEntity<Map<String, Object>> listerConfigs() {
        return ResponseEntity.ok(Map.of("content", service.listerConfigs()));
    }

    @GetMapping("/especes/{espece}")
    public ResponseEntity<Map<String, Object>> getConfig(@PathVariable Espece espece) {
        return ResponseEntity.ok(Map.of("data", service.getConfig(espece)));
    }

    @PutMapping("/especes/{espece}")
    public ResponseEntity<Map<String, Object>> modifierConfig(@PathVariable Espece espece, @RequestBody ConfigEspeceRequest request) {
        return ResponseEntity.ok(Map.of("data", service.modifierConfig(espece, request)));
    }

    @GetMapping("/courbes/{espece}")
    public ResponseEntity<Map<String, Object>> courbe(@PathVariable Espece espece) {
        return ResponseEntity.ok(Map.of("content", service.courbe(espece)));
    }

    @PostMapping("/courbes")
    public ResponseEntity<Map<String, Object>> ajouterPointsCourbe(@RequestBody List<CourbeCroissanceRequest> requests) {
        List<CourbeCroissanceReference> resultats = service.ajouterPointsCourbe(requests);
        return ResponseEntity.ok(Map.of("content", resultats, "totalElements", resultats.size()));
    }

    @GetMapping("/evenements/{espece}")
    public ResponseEntity<Map<String, Object>> evenements(@PathVariable Espece espece) {
        return ResponseEntity.ok(Map.of("content", service.evenements(espece)));
    }

    @PostMapping("/evenements")
    public ResponseEntity<Map<String, Object>> creerEvenement(@RequestBody TypeEvenementCustomRequest request) {
        TypeEvenementCustom resultat = service.creerEvenement(request);
        return ResponseEntity.ok(Map.of("data", resultat));
    }
}
