package com.reseau_partage.animaux.dto.misebas;

import com.reseau_partage.core.entities.TypeMiseBas;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MiseBaRequest(
        @NotNull Long truieId,
        @NotNull Long saillieId,
        String nom,
        @NotNull LocalDateTime dateMiseBasReelle,
        Integer dureeMiseBaMinutes,
        @NotNull TypeMiseBas typeMiseBas,

        // ── Résultats ─────────────────────────────────────────────────────────
        @NotNull @Min(0) Integer nbNesVivants,
        @Min(0) Integer nbMortNes,
        @Min(0) Integer nbMomifies,
        BigDecimal poidsMoyenNaissanceKg,
        BigDecimal poidsMinNaissanceKg,
        BigDecimal poidsMaxNaissanceKg,

        // ── Allaitement ───────────────────────────────────────────────────────
        Integer nbPorceletsAllaites,

        // ── Destination des porcelets ─────────────────────────────────────────
        Long bandeDestinationId,

        String veterinaireNom,
        String operateurNom,
        String notes
) {}
