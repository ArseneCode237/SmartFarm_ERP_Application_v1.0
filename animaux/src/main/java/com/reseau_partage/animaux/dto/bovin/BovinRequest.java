package com.reseau_partage.animaux.dto.bovin;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.reseau_partage.core.entities.CategorieBovine;
import com.reseau_partage.core.entities.Sexe;
import com.reseau_partage.core.entities.TypeProduction;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BovinRequest(
        String codeBoucle,
        String codeRfid,
        String nom,
        @NotNull @Size(max = 100) String race,
        @Size(max = 50) String souche,
        Boolean castre,
        Boolean dejaVeule,
        @Size(max = 50) String couleurRobe,
        @Size(max = 50) String marqueFer,
        @Size(max = 50) String dentition,
        TypeProduction typeProduction,
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
