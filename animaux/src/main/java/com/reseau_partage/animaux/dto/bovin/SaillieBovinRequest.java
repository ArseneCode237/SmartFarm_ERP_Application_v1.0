package com.reseau_partage.animaux.dto.bovin;

import java.time.LocalDate;

import com.reseau_partage.core.entities.TypeSaillie;

import jakarta.validation.constraints.NotNull;

public record SaillieBovinRequest(
        @NotNull Long vacheId,
        Long taureauId,
        @NotNull TypeSaillie typeSaillie,
        @NotNull LocalDate dateSaillie,
        String semenceRace,
        String semenceReference,
        String operateurNom,
        String notes) {
}
