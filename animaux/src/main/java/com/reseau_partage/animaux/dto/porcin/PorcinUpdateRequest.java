package com.reseau_partage.animaux.dto.porcin;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.reseau_partage.core.entities.Provenance;
import com.reseau_partage.core.entities.Sexe;

import jakarta.validation.constraints.DecimalMin;

public record PorcinUpdateRequest(
        String nom,
        String codeBoucle,
        String codeRfid,
        Sexe sexe,
        String race,
        LocalDate dateNaissance,
        LocalDate dateEntree,
        @DecimalMin("0.0") BigDecimal poidsEntreeKg,
        @DecimalMin("0.0") BigDecimal poidsActuelKg,
        Provenance provenance,
        String fournisseurNom,
        BigDecimal prixUnitaire,
        String notes
) {}
