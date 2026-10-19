package com.reseau_partage.animaux.dto.bovin;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.reseau_partage.core.entities.CategorieBovine;
import com.reseau_partage.core.entities.Sexe;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BovinRequest(
        String codeBoucle,
        String codeRfid,
        String nom,
        @NotNull @Size(max = 100) String race,
        @NotNull Sexe sexe,
        LocalDate dateNaissance,
        @NotNull LocalDate dateEntree,
        @NotNull @DecimalMin("0.0") BigDecimal poidsEntreeKg,
        @NotNull Long structureId,
        @NotNull CategorieBovine categorieBovine,
        Long mereId,
        Long pereId,
        BigDecimal poidsCibleAdulteKg,
        BigDecimal productionLaitCibleJKg,
        String notes) {
}
