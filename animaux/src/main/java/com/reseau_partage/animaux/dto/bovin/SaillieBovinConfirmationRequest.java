package com.reseau_partage.animaux.dto.bovin;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

public record SaillieBovinConfirmationRequest(
        @NotNull Boolean confirmee,
        @NotNull LocalDate dateConfirmationEcho,
        String motifEchec) {
}
