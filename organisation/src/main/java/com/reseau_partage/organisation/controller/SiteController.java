package com.reseau_partage.organisation.controller;

import com.reseau_partage.core.entities.StatutSite;
import com.reseau_partage.organisation.dto.ImportResultResponse;
import com.reseau_partage.organisation.dto.SiteRequest;
import com.reseau_partage.organisation.dto.StatutRequest;
import com.reseau_partage.organisation.service.OrganisationService;
import com.reseau_partage.organisation.service.SiteImportExportService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/organisation/sites")
public class SiteController {

    private final OrganisationService service;
    private final SiteImportExportService importExportService;

    public SiteController(OrganisationService service,
            SiteImportExportService importExportService) {
        this.service = service;
        this.importExportService = importExportService;
    }

    /**
     * POST /api/organisation/sites
     * Créer un nouveau site.
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody SiteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createSite(request));
    }

    /**
     * GET /api/organisation/sites/ferme/{fermeId}
     * Lister tous les sites d'une ferme.
     */
    @GetMapping("/ferme/{fermeId}")
    public ResponseEntity<List<Map<String, Object>>> listByFerme(@PathVariable Long fermeId) {
        return ResponseEntity.ok(service.listSites(fermeId));
    }

    /**
     * GET /api/organisation/sites/{id}
     * Détail d'un site avec compteur de structures.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.getSite(id));
    }

    /**
     * PUT /api/organisation/sites/{id}
     * Modifier un site.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id,
            @Valid @RequestBody SiteRequest request) {
        return ResponseEntity.ok(service.updateSite(id, request));
    }

    /**
     * PATCH /api/organisation/sites/{id}/statut
     * Changer le statut d'un site (ACTIF, INACTIF, ARCHIVE).
     * Un passage à ARCHIVE cascade vers toutes les structures du site.
     *
     * Corps attendu : { "statut": "INACTIF" }
     */
    @PatchMapping("/{id}/statut")
    public ResponseEntity<Map<String, Object>> changeStatut(@PathVariable Long id,
            @Valid @RequestBody StatutRequest request) {
        StatutSite statut;
        try {
            statut = StatutSite.valueOf(request.statut().trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Statut invalide : " + request.statut() + ". Valeurs acceptées : ACTIF, INACTIF, ARCHIVE.");
        }
        return ResponseEntity.ok(service.siteStatus(id, statut));
    }

    /**
     * GET /api/organisation/sites/{id}/statistiques
     * Statistiques agrégées du site (structures par type, animaux, employés).
     */
    @GetMapping("/{id}/statistiques")
    public ResponseEntity<Map<String, Object>> stats(@PathVariable Long id) {
        return ResponseEntity.ok(service.siteStats(id));
    }

    /**
     * GET /api/organisation/sites/{id}/duplicate
     * Récupère les données d'un site existant pour pré-remplir un nouveau formulaire.
     * Le nom est préfixé avec "Copie - " pour indiquer clairement la duplication.
     * Le site reste rattaché à la même ferme (modifiable si besoin).
     * Aucun ID n'est renvoyé : il sera généré à la création.
     */
    @GetMapping("/{id}/duplicate")
    public ResponseEntity<SiteRequest> duplicate(@PathVariable Long id) {
        return ResponseEntity.ok(service.duplicateSite(id));
    }

    // ═══════════════════════════════════════════════════════════════════════
    // EXPORT / TEMPLATE / IMPORT
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * GET /api/organisation/sites/export
     * Exporte tous les sites (d'une ferme si fermeId fourni) en CSV ou Excel.
     *
     * Query params :
     *   - fermeId (optionnel) : filtrer sur une ferme spécifique
     *   - format  (optionnel) : "csv" | "excel" — défaut = "excel"
     */
    @GetMapping(value = "/export")
    public ResponseEntity<byte[]> export(
            @RequestParam(required = false) Long fermeId,
            @RequestParam(required = false, defaultValue = "excel") String format,
            Authentication authentication) {

        SiteImportExportService.ExportResult result =
                importExportService.exportSites(fermeId, format, authentication.getName());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(result.contentType()));
        headers.setContentDispositionFormData("attachment", result.filename());
        headers.setContentLength(result.data().length);
        return new ResponseEntity<>(result.data(), headers, HttpStatus.OK);
    }

    /**
     * GET /api/organisation/sites/import/template
     * Télécharge le template Excel vierge pour l'import de sites.
     * Colonne "nom*" avec validation non vide ; ligne commentaire en gris.
     */
    @GetMapping(value = "/import/template")
    public ResponseEntity<byte[]> template() {
        SiteImportExportService.ExportResult result = importExportService.buildTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(result.contentType()));
        headers.setContentDispositionFormData("attachment", result.filename());
        headers.setContentLength(result.data().length);
        return new ResponseEntity<>(result.data(), headers, HttpStatus.OK);
    }

    /**
     * POST /api/organisation/sites/import
     * Importe un fichier CSV ou Excel de sites.
     *
     * Query : fermeId (obligatoire) — la ferme cible pour TOUS les sites
     * Body  : multipart/form-data — champ "file" contenant le fichier
     */
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImportResultResponse> importer(
            @RequestParam Long fermeId,
            @RequestPart("file") MultipartFile file,
            Authentication authentication) {

        ImportResultResponse result =
                importExportService.importerSites(file, fermeId, authentication.getName());
        HttpStatus status = result.success() && result.importes() > 0
                ? HttpStatus.CREATED
                : HttpStatus.OK;
        return ResponseEntity.status(status).body(result);
    }
}
