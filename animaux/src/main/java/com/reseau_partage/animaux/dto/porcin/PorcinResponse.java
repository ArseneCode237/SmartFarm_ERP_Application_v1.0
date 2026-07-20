package com.reseau_partage.animaux.dto.porcin;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.reseau_partage.animaux.dto.saillie.SaillieResponse;
import com.reseau_partage.core.entities.Provenance;
import com.reseau_partage.core.entities.Sexe;
import com.reseau_partage.core.entities.StatutAnimal;
import com.reseau_partage.core.entities.StatutReproductifPorcin;

public record PorcinResponse(
        // ── Animal de base ────────────────────────────────────────────────────
        Long id,
        String codeUnique,
        String codeBoucle,
        String codeRfid,
        String nom,
        String race,
        Sexe sexe,
        LocalDate dateNaissance,
        LocalDate dateEntree,
        Integer ageJours,
        BigDecimal poidsEntreeKg,
        BigDecimal poidsActuelKg,
        StatutAnimal statut,
        Long structureId,
        String structureNom,
        String siteNom,
        Provenance provenance,
        String fournisseurNom,
        BigDecimal prixUnitaire,

        // ── Profil reproductif ────────────────────────────────────────────────
        StatutReproductifPorcin statutReproductif,
        LocalDate dateDebutStatutActuel,
        Integer nbPorteesTotal,
        Integer nbPorceletsTotalNesVivants,
        Integer nbPorceletsTotalSevres,
        BigDecimal moyNesVivantsParPortee,
        BigDecimal moyPoidsSevrageKg,

        // ── Cycle en cours ────────────────────────────────────────────────────
        LocalDate dateMiseBasPrevue,
        LocalDate dateSevragePrevu,
        LocalDate dateRetourChaleurEstimee,
        SaillieResponse saillieActive,

        // ── Origine ───────────────────────────────────────────────────────────
        Long bandeOrigineId,
        String bandeOrigineNom,
        LocalDate dateExtractionBande,
        BigDecimal poidsSelectionKg,

        LocalDateTime dateCreation
) {}
