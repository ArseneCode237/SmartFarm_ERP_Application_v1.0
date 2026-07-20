package com.reseau_partage.animaux.dto.porcin;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.reseau_partage.core.entities.Provenance;
import com.reseau_partage.core.entities.Sexe;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

/** Création directe d'un animal porcin (achat, naissance interne, don). */
public record PorcinRequest(
        // ── Identification ────────────────────────────────────────────────────
        String codeBoucle,
        String codeRfid,
        String nom,
        @NotNull String race,
        @NotNull Sexe sexe,

        // ── Biologie ─────────────────────────────────────────────────────────
        LocalDate dateNaissance,
        @NotNull LocalDate dateEntree,
        @NotNull @DecimalMin("0.0") BigDecimal poidsEntreeKg,

        // ── Localisation ─────────────────────────────────────────────────────
        @NotNull Long siteId,
        @NotNull Long structureId,

        // ── Provenance ────────────────────────────────────────────────────────
        @NotNull Provenance provenance,
        String fournisseurNom,
        BigDecimal coutAchat,

        // ── Généalogie ────────────────────────────────────────────────────────
        Long mereId,
        Long pereId,
        Long miseBaOrigineId,

        String notes
) {}
