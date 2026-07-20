package com.reseau_partage.animaux.dto.porcin;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.reseau_partage.core.entities.Sexe;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record ExtractionBandeRequest(
        @NotNull Long bandeId,
        @NotNull LocalDate dateExtraction,
        /** ID de la structure de destination pour tous les animaux extraits (optionnel — peut varier par animal). */
        Long structureDestinationId,
        @NotEmpty List<AnimalExtraitDto> animaux
) {
    public record AnimalExtraitDto(
            String codeBoucle,
            String codeRfid,
            @NotNull Sexe sexe,
            /** Poids au moment de la sélection (kg). */
            @NotNull BigDecimal poidsKg,
            /** Structure individuelle spécifique, prioritaire sur structureDestinationId. */
            Long structureId,
            String notes
    ) {}
}
