package com.reseau_partage.organisation.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StructureRequest(
        // ── Champs communs à tous les types ──────────────────────────────────
        @NotNull  Long siteId,
        @NotBlank String nom,
        // BATIMENT | ENCLOS | ETANG | ENTREPOT | PARCELLE | POULAILLER | PORCHERIE
        @NotBlank String typeStructure,
        String description,
        @DecimalMin("0.0") BigDecimal superficieM2,
        BigDecimal latitude,
        BigDecimal longitude,

        // ── Batiment ─────────────────────────────────────────────────────────
        Integer capaciteMaxAnimaux,         // aussi Enclos, Poulailler, Porcherie
        String  typeVentilation,            // aussi Poulailler, Porcherie
        Integer dureeVideSanitaireJours,    // aussi Poulailler, Porcherie
        Integer nombreRangees,              // aussi Poulailler
        String  systemeAbreuvement,         // aussi Poulailler, Porcherie

        // ── Enclos ───────────────────────────────────────────────────────────
        String  typeCloture,
        Boolean accesEau,
        String  especesCompatibles,

        // ── Etang ────────────────────────────────────────────────────────────
        BigDecimal volumeM3,
        BigDecimal profondeurM,
        String     systemeAeration,
        BigDecimal temperatureCibleCelsius,
        BigDecimal phCible,

        // ── Entrepot ─────────────────────────────────────────────────────────
        BigDecimal capaciteTonnes,
        Boolean    temperatureControlee,
        BigDecimal temperatureMinCelsius,
        BigDecimal temperatureMaxCelsius,

        // ── Parcelle ─────────────────────────────────────────────────────────
        String typeSol,                     // aussi Porcherie
        String cultureActuelle,
        String systemeIrrigation,
        String coordonneesPolygone,

        // ── Poulailler ───────────────────────────────────────────────────────
        // typeProduction : PONTE | CHAIR | MIXTE
        String typeProduction,
        String systemeChauffage,

        // ── Porcherie ────────────────────────────────────────────────────────
        // systemeEvacuation : LISIER | FUMIER | BIOGAZ
        String  systemeEvacuation,
        Integer nombreCases
) {}
