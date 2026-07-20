package com.reseau_partage.animaux.dto.porcin;

import com.reseau_partage.core.entities.Provenance;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PorcinUpdateRequest(
        String nom,
        String codeBoucle,
        String codeRfid,
        String race,
        LocalDate dateNaissance,
        LocalDate dateEntree,
        @DecimalMin("0.0") BigDecimal poidsEntreeKg,
        @DecimalMin("0.0") BigDecimal poidsActuelKg,
        Provenance provenance,
        String fournisseurNom,
        String notes
) {}
