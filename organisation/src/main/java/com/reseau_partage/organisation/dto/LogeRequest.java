package com.reseau_partage.organisation.dto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LogeRequest(
        @NotBlank String code,
        @NotBlank String nom,
        String description,
        @NotNull @Min(1) Integer capaciteMaxAnimaux,
        @DecimalMin("0.0") BigDecimal superficieM2,
        @NotNull Long batimentId,
        Long bandeId,
        List<Long> animauxIds
) {}
