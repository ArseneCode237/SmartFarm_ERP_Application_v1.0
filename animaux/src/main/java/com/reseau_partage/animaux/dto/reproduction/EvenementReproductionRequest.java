package com.reseau_partage.animaux.dto.reproduction;

import com.reseau_partage.core.entities.TypeReproduction;

import java.time.LocalDate;

public record EvenementReproductionRequest(
        Long femelleId,
        Long maleId,
        TypeReproduction type,
        LocalDate dateSaillie,
        String notes
) {
}
