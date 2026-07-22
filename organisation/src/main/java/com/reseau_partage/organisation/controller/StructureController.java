package com.reseau_partage.organisation.controller;

import com.reseau_partage.core.entities.StatutStructure;
import com.reseau_partage.organisation.dto.StatutRequest;
import com.reseau_partage.organisation.dto.StructureRequest;
import com.reseau_partage.organisation.service.OrganisationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/organisation/structures")
public class StructureController {

    private final OrganisationService service;

    public StructureController(OrganisationService service) {
        this.service = service;
    }

    /**
     * POST /api/organisation/structures
     * Créer une structure (Batiment, Enclos, Etang, Entrepot, Parcelle).
     * Le champ "typeStructure" détermine le sous-type instancié.
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody StructureRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createStructure(request));
    }

    /**
     * GET /api/organisation/structures/site/{siteId}
     * Lister toutes les structures d'un site.
     */
    @GetMapping("/site/{siteId}")
    public ResponseEntity<List<Map<String, Object>>> listBySite(@PathVariable Long siteId) {
        return ResponseEntity.ok(service.listStructuresForSite(siteId));
    }

    /**
     * GET /api/organisation/structures/ferme/{fermeId}
     * Lister toutes les structures de tous les sites d'une ferme.
     */
    @GetMapping("/ferme/{fermeId}")
    public ResponseEntity<List<Map<String, Object>>> listByFerme(@PathVariable Long fermeId) {
        return ResponseEntity.ok(service.listStructuresForFerme(fermeId));
    }

    /**
     * GET /api/organisation/structures/{id}
     * Détail d'une structure (champs communs + attributs spécifiques selon le
     * type).
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.getStructure(id));
    }

    /**
     * GET /api/organisation/structures/{id}/animaux
     * Lister les animaux rattachés à une structure.
     */
    @GetMapping("/{id}/animaux")
    public ResponseEntity<List<Map<String, Object>>> listAnimals(@PathVariable Long id) {
        return ResponseEntity.ok(service.listAnimalsForStructure(id));
    }

    /**
     * PUT /api/organisation/structures/{id}
     * Modifier une structure. Le type de structure ne peut pas être changé.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id,
            @Valid @RequestBody StructureRequest request) {
        return ResponseEntity.ok(service.updateStructure(id, request));
    }

    /**
     * PATCH /api/organisation/structures/{id}/statut
     * Changer le statut selon les transitions autorisées :
     * ACTIF → VIDE_SANITAIRE | ARCHIVE
     * VIDE_SANITAIRE → EN_DESINFECTION
     * EN_DESINFECTION → PRET
     * PRET → ACTIF
     *
     * Corps attendu : { "statut": "EN_DESINFECTION" }
     */
    @PatchMapping("/{id}/statut")
    public ResponseEntity<Map<String, Object>> changeStatut(@PathVariable Long id,
            @Valid @RequestBody StatutRequest request) {
        StatutStructure statut;
        try {
            statut = StatutStructure.valueOf(request.statut().trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Statut invalide : " + request.statut() +
                            ". Valeurs acceptées : ACTIF, VIDE_SANITAIRE, EN_DESINFECTION, PRET, ARCHIVE.");
        }
        return ResponseEntity.ok(service.structureStatus(id, statut));
    }

    /**
     * POST /api/organisation/structures/{id}/vide-sanitaire
     * Démarrer le vide sanitaire d'une structure (Batiment uniquement en pratique).
     * Passe le statut à VIDE_SANITAIRE et enregistre la date de début.
     */
    @PostMapping("/{id}/vide-sanitaire")
    public ResponseEntity<Map<String, Object>> startSanitaryVacancy(@PathVariable Long id) {
        return ResponseEntity.ok(service.startSanitaryVacancy(id));
    }

    /**
     * GET /api/organisation/structures/{id}/occupation
     * Taux d'occupation d'une structure animale (Batiment ou Enclos).
     * Retourne capaciteMaxAnimaux, animauxPresents, tauxOccupation (%) et
     * niveauAlerte.
     */
    @GetMapping("/{id}/occupation")
    public ResponseEntity<Map<String, Object>> occupancy(@PathVariable Long id) {
        return ResponseEntity.ok(service.occupancy(id));
    }
}
