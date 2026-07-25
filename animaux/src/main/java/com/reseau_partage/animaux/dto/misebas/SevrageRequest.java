package com.reseau_partage.animaux.dto.misebas;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SevrageRequest(
        @NotNull LocalDate dateSevrageReel,
        @NotNull @Min(0) Integer nbSevres,
        @NotNull BigDecimal poidsMoyenSevrageKg,
        Long bandeDestinationId,
        String operateurNom,
        String notes
) {}
