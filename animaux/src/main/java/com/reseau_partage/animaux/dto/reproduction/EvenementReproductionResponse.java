package com.reseau_partage.animaux.dto.reproduction;

import com.reseau_partage.core.entities.StatutGestation;
import com.reseau_partage.core.entities.TypeReproduction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record EvenementReproductionResponse(
        Long id,
        Long femelleId,
        String femelleCode,
        Long maleId,
        String maleCode,
        TypeReproduction type,
        LocalDate dateSaillie,
        LocalDate dateMiseBasPrevue,
        LocalDate dateMiseBasReelle,
        StatutGestation statut,
        Integer nombreNesVivants,
        Integer nombreNesMorts,
        BigDecimal poidsMoyenNaissanceKg,
        LocalDate dateSevragePrevu,
        LocalDate dateSevrageReel,
        BigDecimal poidsAuSevrageKg,
        String notes,
        LocalDateTime dateCreation
) {
}
