package com.reseau_partage.animaux.dto.bovin;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ControleLaitierRequest(
        @NotNull Long animalId,
        @NotNull LocalDate dateControle,
        @DecimalMin("0.0") BigDecimal laitMatinKg,
        @DecimalMin("0.0") BigDecimal laitMidiKg,
        @DecimalMin("0.0") BigDecimal laitSoirKg,
        @DecimalMin("0.0") BigDecimal laitCibleJourKg,
        @DecimalMin("0.0") BigDecimal tauxButyreuxPct,
        @DecimalMin("0.0") BigDecimal tauxProteiquePct,
        @Min(0) Integer cellulesSomatiquesMilliers,
        String operateurNom,
        String notes) {
}
