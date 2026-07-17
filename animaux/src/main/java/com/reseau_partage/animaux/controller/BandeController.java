package com.reseau_partage.animaux.controller;

import com.reseau_partage.animaux.dto.bande.BandeRequest;
import com.reseau_partage.animaux.dto.bande.BandeResponse;
import com.reseau_partage.animaux.service.BandeService;
import com.reseau_partage.core.entities.Espece;
import com.reseau_partage.core.entities.StatutBande;
import com.reseau_partage.core.entities.TypeMouvement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/animaux/bandes")
public class BandeController {

    private final BandeService service;

    public BandeController(BandeService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody BandeRequest request, Authentication authentication) {
        BandeResponse response = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("data", response));
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(required = false) Long structureId,
            @RequestParam(required = false) Long siteId,
            @RequestParam(required = false) Long fermeId,
            @RequestParam(required = false) Espece espece,
            @RequestParam(required = false) StatutBande statut,
            org.springframework.data.domain.Pageable pageable) {
        var page = service.list(structureId, siteId, fermeId, espece, statut, pageable);
        return ResponseEntity.ok(Map.of(
                "content", page.getContent(),
                "totalElements", page.getTotalElements(),
                "totalPages", page.getTotalPages()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> get(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("data", service.get(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @Valid @RequestBody BandeRequest request) {
        return ResponseEntity.ok(Map.of("data", service.update(id, request)));
    }

    @PostMapping("/{id}/sortie")
    public ResponseEntity<Void> sortie(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        TypeMouvement type = TypeMouvement.valueOf(((String) body.get("typeMouvement")).toUpperCase());
        LocalDate date = LocalDate.parse((String) body.get("dateSortie"));
        Integer quantite = body.get("quantite") != null ? ((Number) body.get("quantite")).intValue() : null;
        BigDecimal poids = body.get("poidsKg") != null ? new BigDecimal(body.get("poidsKg").toString()) : null;
        BigDecimal prix = body.get("prixUnitaire") != null ? new BigDecimal(body.get("prixUnitaire").toString()) : null;
        String motif = (String) body.get("motif");
        String operateur = (String) body.get("operateurNom");
        service.sortieCollective(id, type, date, quantite, poids, prix, motif, operateur);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/transfert")
    public ResponseEntity<Void> transfert(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Long destination = Long.parseLong(body.get("structureDestinationId").toString());
        String operateur = (String) body.get("operateurNom");
        String motif = (String) body.get("motif");
        service.transfert(id, destination, operateur, motif);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/cloturer")
    public ResponseEntity<Map<String, Object>> cloturer(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("data", service.cloturer(id)));
    }

    @PostMapping("/{id}/performances")
    public ResponseEntity<Void> performances(@PathVariable Long id) {
        service.mettreAJourPerformances(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/echeances")
    public ResponseEntity<Map<String, Object>> echeances(
            @RequestParam(required = false) Long fermeId,
            @RequestParam(defaultValue = "30") int jours) {
        if (fermeId == null) {
            return ResponseEntity.ok(Map.of("content", List.of()));
        }
        return ResponseEntity.ok(Map.of("content", service.echeances(fermeId, jours)));
    }
}
