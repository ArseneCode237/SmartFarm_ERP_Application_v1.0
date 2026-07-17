package com.reseau_partage.animaux.controller;

import com.reseau_partage.animaux.dto.animal.AnimalRequest;
import com.reseau_partage.animaux.dto.animal.AnimalResponse;
import com.reseau_partage.animaux.exception.ResourceNotFoundException;
import com.reseau_partage.animaux.service.AnimalService;
import com.reseau_partage.animaux.service.AuditService;
import com.reseau_partage.animaux.service.ExportService;
import com.reseau_partage.core.entities.Espece;
import com.reseau_partage.core.entities.ModeSuivi;
import com.reseau_partage.core.entities.Sexe;
import com.reseau_partage.core.entities.StatutAnimal;
import com.reseau_partage.core.entities.TypeMouvement;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/animaux/animaux")
public class AnimalController {

    private final AnimalService service;
    private final AuditService auditService;
    private final ExportService exportService;

    public AnimalController(AnimalService service, AuditService auditService, ExportService exportService) {
        this.service = service;
        this.auditService = auditService;
        this.exportService = exportService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody AnimalRequest request, Authentication authentication) {
        AnimalResponse response = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("data", response));
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(required = false) Espece espece,
            @RequestParam(required = false) StatutAnimal statut,
            @RequestParam(required = false) ModeSuivi modeSuivi,
            @RequestParam(required = false) Long structureId,
            @RequestParam(required = false) Long siteId,
            @RequestParam(required = false) Long fermeId,
            @RequestParam(required = false) Long bandeId,
            org.springframework.data.domain.Pageable pageable) {
        var page = service.list(espece, statut, modeSuivi, structureId, siteId, fermeId, bandeId, pageable);
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
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @Valid @RequestBody AnimalRequest request) {
        return ResponseEntity.ok(Map.of("data", service.update(id, request)));
    }

    @PostMapping("/{id}/sortie")
    public ResponseEntity<Void> sortie(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        TypeMouvement type = TypeMouvement.valueOf(((String) body.get("typeMouvement")).toUpperCase());
        LocalDate date = LocalDate.parse((String) body.get("dateSortie"));
        BigDecimal poids = body.get("poidsKg") != null ? new BigDecimal(body.get("poidsKg").toString()) : null;
        BigDecimal prix = body.get("prixUnitaire") != null ? new BigDecimal(body.get("prixUnitaire").toString()) : null;
        String motif = (String) body.get("motif");
        String causeMort = (String) body.get("causeMort");
        String operateur = (String) body.get("operateurNom");
        service.declarerSortie(id, type, date, poids, prix, motif, causeMort, operateur);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/transfert")
    public ResponseEntity<Void> transfert(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Long destination = Long.parseLong(body.get("structureDestinationId").toString());
        String operateur = (String) body.get("operateurNom");
        String motif = (String) body.get("motif");
        service.transferer(id, destination, operateur, motif);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/genealogie")
    public ResponseEntity<Map<String, Object>> genealogie(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("data", service.genealogie(id)));
    }

    @GetMapping("/{id}/mouvements")
    public ResponseEntity<Map<String, Object>> mouvements(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("data", service.historique(id)));
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> search(@RequestParam String q) {
        return ResponseEntity.ok(Map.of("data", service.recherche(q)));
    }

    @GetMapping("/surveiller")
    public ResponseEntity<Map<String, Object>> surveiller(@RequestParam(required = false) Long fermeId) {
        if (fermeId == null) {
            return ResponseEntity.ok(Map.of("content", List.of()));
        }
        return ResponseEntity.ok(Map.of("content", service.animauxASurveiller(fermeId)));
    }

    @GetMapping("/{id}/audit")
    public ResponseEntity<Map<String, Object>> audit(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("content", auditService.journal(id)));
    }

    @GetMapping("/{id}/bilan-vie")
    public ResponseEntity<Map<String, Object>> bilanVie(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("data", auditService.bilanVie(id)));
    }

    @GetMapping("/{id}/export/pdf")
    public ResponseEntity<byte[]> exportPdf(@PathVariable Long id) {
        byte[] pdf = exportService.exporterBilanViePDF(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=animal-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/export/csv")
    public ResponseEntity<byte[]> exportCsv(
            @RequestParam(required = false) Espece espece,
            @RequestParam(required = false) StatutAnimal statut,
            @RequestParam(required = false) ModeSuivi modeSuivi,
            @RequestParam(required = false) Sexe sexe,
            @RequestParam(required = false) Long structureId,
            @RequestParam(required = false) Long siteId,
            @RequestParam(required = false) Long fermeId,
            @RequestParam(required = false) Long bandeId,
            @RequestParam(required = false) Integer ageMinJours,
            @RequestParam(required = false) Integer ageMaxJours,
            @RequestParam(required = false) BigDecimal poidsMinKg,
            @RequestParam(required = false) BigDecimal poidsMaxKg,
            @RequestParam(required = false) LocalDate dateEntreeDebut,
            @RequestParam(required = false) LocalDate dateEntreeFin) {
        byte[] csv = exportService.exporterListeCSV(espece, statut, modeSuivi, structureId, siteId, fermeId,
                bandeId, sexe, ageMinJours, ageMaxJours, poidsMinKg, poidsMaxKg, dateEntreeDebut, dateEntreeFin);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=animaux.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }

    @GetMapping("/recherche-avancee")
    public ResponseEntity<Map<String, Object>> rechercheAvancee(
            @RequestParam(required = false) Espece espece,
            @RequestParam(required = false) StatutAnimal statut,
            @RequestParam(required = false) ModeSuivi modeSuivi,
            @RequestParam(required = false) Sexe sexe,
            @RequestParam(required = false) Long structureId,
            @RequestParam(required = false) Long siteId,
            @RequestParam(required = false) Long fermeId,
            @RequestParam(required = false) Long bandeId,
            @RequestParam(required = false) Integer ageMinJours,
            @RequestParam(required = false) Integer ageMaxJours,
            @RequestParam(required = false) BigDecimal poidsMinKg,
            @RequestParam(required = false) BigDecimal poidsMaxKg,
            @RequestParam(required = false) LocalDate dateEntreeDebut,
            @RequestParam(required = false) LocalDate dateEntreeFin) {
        List<?> resultats = exportService.rechercheAvancee(espece, statut, modeSuivi, structureId, siteId,
                fermeId, bandeId, sexe, ageMinJours, ageMaxJours, poidsMinKg, poidsMaxKg, dateEntreeDebut, dateEntreeFin);
        return ResponseEntity.ok(Map.of("content", resultats, "totalElements", resultats.size()));
    }
}
