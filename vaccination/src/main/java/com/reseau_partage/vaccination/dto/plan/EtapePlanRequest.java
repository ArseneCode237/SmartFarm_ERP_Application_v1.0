package com.reseau_partage.vaccination.dto.plan;

import java.math.BigDecimal;

import com.reseau_partage.core.entities.enumtypes.VoieAdministration;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record EtapePlanRequest(
        Long id,
        @NotNull Long vaccinId,
        @NotNull @Min(1) Integer ordreEtape,
        @NotNull @Min(0) Integer ageCibleJours,
        @Min(0) Integer toleranceJours,
        BigDecimal doseMl,
        VoieAdministration voieAdministration,
        String instructions) {
}
