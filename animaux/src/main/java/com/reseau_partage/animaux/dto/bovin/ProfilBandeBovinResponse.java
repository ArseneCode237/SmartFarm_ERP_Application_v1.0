package com.reseau_partage.animaux.dto.bovin;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.reseau_partage.core.entities.TypeProductionBovin;

public record ProfilBandeBovinResponse(
        Long bandeId,
        TypeProductionBovin typeProductionBovin,
        String race,
        BigDecimal poidsCibleSortieKg,
        Integer ageCibleAbattageMois,
        Integer gmqCibleG,
        BigDecimal fcrCible,
        Integer dureeEngraissementJours,
        BigDecimal alimentConsommeTotalKg,
        LocalDate dateAbattagePrevue,
        LocalDate dateAbattageReelle,
        BigDecimal rendementCarcassePct,
        LocalDateTime dateCreation,
        LocalDateTime dateModification) {
}
