package com.reseau_partage.animaux.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.reseau_partage.animaux.dto.pesee.IndicateursBandeResponse;
import com.reseau_partage.animaux.dto.pesee.PeseeRequest;
import com.reseau_partage.animaux.dto.pesee.PeseeResponse;
import com.reseau_partage.animaux.dto.pesee.PrevisionSortieResponse;
import com.reseau_partage.animaux.service.PeseeService;

@RestController
@RequestMapping("/api/animaux/pesees")
public class PeseeController {

    private final PeseeService service;

    public PeseeController(PeseeService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> enregistrer(@RequestBody PeseeRequest request) {
        PeseeResponse data = service.enregistrerPesee(request);
        return ResponseEntity.ok(Map.of("data", data, "message", "Pesée enregistrée avec succès. Poids : " + request.poidsKg() + " kg, date : " + data.datePesee() + "."));
    }

    @PostMapping("/bande/{id}")
    public ResponseEntity<Map<String, Object>> peseeCollective(@PathVariable Long id, @RequestBody PeseeRequest request) {
        List<PeseeResponse> resultats = service.peseeCollectiveBande(id, request);
        return ResponseEntity.ok(Map.of("content", resultats, "totalElements", resultats.size(), "message", "Pesée collective effectuée sur la bande id=" + id + ". " + resultats.size() + " animaux pesés."));
    }

    @GetMapping("/animal/{id}")
    public ResponseEntity<Map<String, Object>> historiqueAnimal(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("content", service.historiqueAnimal(id)));
    }

    @GetMapping("/bande/{id}")
    public ResponseEntity<Map<String, Object>> historiqueBande(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("content", service.historiqueBande(id)));
    }

    @GetMapping("/courbe/{animalId}")
    public ResponseEntity<Map<String, Object>> courbe(@PathVariable Long animalId) {
        return ResponseEntity.ok(Map.of("content", service.courbeCroissance(animalId)));
    }

    @GetMapping("/sous-performeurs")
    public ResponseEntity<Map<String, Object>> sousPerformeurs(@RequestParam(required = false) Long fermeId) {
        return ResponseEntity.ok(Map.of("content", service.sousPerformeurs(fermeId)));
    }

    @GetMapping("/indicateurs/{bandeId}")
    public ResponseEntity<Map<String, Object>> indicateurs(@PathVariable Long bandeId) {
        IndicateursBandeResponse indicateurs = service.indicateursBande(bandeId);
        return ResponseEntity.ok(Map.of("data", indicateurs, "message", "Indicateurs calculés pour la bande id=" + bandeId + ". Poids moyen : " + indicateurs.poidsMoyen() + " kg, CV : " + indicateurs.cvPct() + "%."));
    }

    @GetMapping("/previsions/{bandeId}")
    public ResponseEntity<Map<String, Object>> previsions(@PathVariable Long bandeId) {
        PrevisionSortieResponse prevision = service.prevoirSortie(bandeId);
        return ResponseEntity.ok(Map.of("data", prevision, "message", "Prévision calculée pour la bande id=" + bandeId + ". Sortie prévue le " + prevision.datePrevueSortie() + ", poids cible : " + prevision.poidsPrevuKg() + " kg."));
    }
}
