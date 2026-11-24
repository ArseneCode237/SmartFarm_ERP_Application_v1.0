package com.reseau_partage.animaux.controller;

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

import com.reseau_partage.animaux.dto.bovin.BandeBovinRequest;
import com.reseau_partage.animaux.dto.bovin.BandeBovinResponse;
import com.reseau_partage.animaux.dto.bovin.BovinCategorieRequest;
import com.reseau_partage.animaux.dto.bovin.BovinRequest;
import com.reseau_partage.animaux.dto.bovin.BovinResponse;
import com.reseau_partage.animaux.dto.bovin.BovinStatutRequest;
import com.reseau_partage.animaux.dto.bovin.GenealogieBovinResponse;
import com.reseau_partage.animaux.dto.pesee.PeseeRequest;
import com.reseau_partage.animaux.dto.pesee.PeseeResponse;
import com.reseau_partage.animaux.service.BandeBovinService;
import com.reseau_partage.animaux.service.BovinService;
import com.reseau_partage.animaux.service.PeseeService;
import com.reseau_partage.animaux.service.SaillieBovinService;
import com.reseau_partage.core.entities.CategorieBovine;
import com.reseau_partage.core.entities.StatutReproductifBovin;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/animaux/bovins")
public class BovinController {

    private final BovinService service;
    private final SaillieBovinService saillieService;
    private final BandeBovinService bandeBovinService;
    private final PeseeService peseeService;

    public BovinController(BovinService service, SaillieBovinService saillieService,
            BandeBovinService bandeBovinService, PeseeService peseeService) {
        this.service = service;
        this.saillieService = saillieService;
        this.bandeBovinService = bandeBovinService;
        this.peseeService = peseeService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody BovinRequest request) {
        BovinResponse bovin = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("data", bovin, "message", "Bovin créé avec succès."));
    }

    /** Création d'une bande bovine (troupeau) + son profil bovin. */
    @PostMapping("/bande")
    public ResponseEntity<Map<String, Object>> creerBandeBovine(@Valid @RequestBody BandeBovinRequest request) {
        BandeBovinResponse data = bandeBovinService.creerBandeBovine(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("data", data, "message", "Bande bovine créée avec succès."));
    }

    /**
     * Profil bovin selon le mode : {@code INDIVIDUEL} (ProfilBovin) ou {@code BANDE} (ProfilBandeBovin).
     */
    @GetMapping("/{id}/profil")
    public ResponseEntity<Map<String, Object>> profil(@PathVariable Long id) {
        return ResponseEntity.ok(service.profil(id));
    }

    /** Pesée collective (mensuelle) d'une bande bovine — déléguée au service de pesée générique. */
    @PostMapping("/{bandeId}/collecte-pesee")
    public ResponseEntity<Map<String, Object>> collectePesee(
            @PathVariable Long bandeId, @RequestBody PeseeRequest request) {
        bandeBovinService.verifierBandeBovine(bandeId);
        List<PeseeResponse> resultats = peseeService.peseeCollectiveBande(bandeId, request);
        return ResponseEntity.ok(Map.of("content", resultats, "totalElements", resultats.size(),
                "message", "Pesée collective effectuée sur la bande bovine id=" + bandeId + ". "
                        + resultats.size() + " animaux pesés."));
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(required = false) CategorieBovine categorie,
            @RequestParam(required = false) StatutReproductifBovin statutReproductif,
            @RequestParam(required = false) Long fermeId) {
        List<BovinResponse> bovins = service.list(categorie, statutReproductif, fermeId);
        return ResponseEntity.ok(Map.of("content", bovins, "totalElements", bovins.size()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> get(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("data", service.get(id)));
    }

    @GetMapping("/{id}/carriere")
    public ResponseEntity<Map<String, Object>> carriere(@PathVariable Long id) {
        return ResponseEntity.ok(service.carriere(id));
    }

    @GetMapping("/{id}/genealogie")
    public ResponseEntity<Map<String, Object>> genealogie(@PathVariable Long id) {
        GenealogieBovinResponse genealogie = service.genealogie(id);
        return ResponseEntity.ok(Map.of("data", genealogie));
    }

    @PatchMapping("/{id}/statut")
    public ResponseEntity<Map<String, Object>> changerStatut(
            @PathVariable Long id, @Valid @RequestBody BovinStatutRequest request) {
        return ResponseEntity.ok(Map.of("data", service.changerStatut(id, request.statut())));
    }

    @PatchMapping("/{id}/categorie")
    public ResponseEntity<Map<String, Object>> changerCategorie(
            @PathVariable Long id, @Valid @RequestBody BovinCategorieRequest request) {
        return ResponseEntity.ok(Map.of("data", service.changerCategorie(id, request.categorie())));
    }

    @PostMapping("/{id}/tarissement")
    public ResponseEntity<Map<String, Object>> tarir(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("data", service.tarir(id)));
    }

    @GetMapping("/alertes/velages-proches")
    public ResponseEntity<Map<String, Object>> velagesProches(
            @RequestParam(defaultValue = "14") int jours) {
        List<BovinResponse> bovins = service.velagesProches(jours);
        return ResponseEntity.ok(Map.of("content", bovins, "totalElements", bovins.size()));
    }

    @GetMapping("/alertes/tarissements")
    public ResponseEntity<Map<String, Object>> tarissements(
            @RequestParam(defaultValue = "3") int jours) {
        List<BovinResponse> bovins = service.tarissementsAEffectuer(jours);
        return ResponseEntity.ok(Map.of("content", bovins, "totalElements", bovins.size()));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> dashboard(@RequestParam Long fermeId) {
        return ResponseEntity.ok(service.dashboard(fermeId));
    }

    @GetMapping("/alertes/saillies-echo")
    public ResponseEntity<Map<String, Object>> echoesEnRetard() {
        var result = saillieService.echoesEnRetard();
        return ResponseEntity.ok(Map.of("content", result, "totalElements", result.size()));
    }

    /** Alertes bovines agrégées : vêlages, tarissements, échos en retard, chutes de lait. */
    @GetMapping("/alertes")
    public ResponseEntity<Map<String, Object>> alertes(
            @RequestParam(required = false) Long fermeId,
            @RequestParam(defaultValue = "14") int joursVelage,
            @RequestParam(defaultValue = "3") int joursTarissement) {
        return ResponseEntity.ok(service.alertes(fermeId, joursVelage, joursTarissement));
    }
}
