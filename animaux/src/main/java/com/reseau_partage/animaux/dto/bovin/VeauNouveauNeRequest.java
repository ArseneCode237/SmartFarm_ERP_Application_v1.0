package com.reseau_partage.animaux.dto.bovin;

import java.math.BigDecimal;

import com.reseau_partage.core.entities.Sexe;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record VeauNouveauNeRequest(
        @NotNull Sexe sexe,
        @NotNull @DecimalMin("0.0") BigDecimal poidsNaissanceKg) {
}
