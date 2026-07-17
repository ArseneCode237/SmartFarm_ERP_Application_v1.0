package com.reseau_partage.animaux.dto.reproduction;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MiseBasRequest(
        LocalDate dateMiseBasReelle,
        Integer nombreNesVivants,
        Integer nombreNesMorts,
        BigDecimal poidsMoyenNaissanceKg,
        String notes,
        String operateurNom
) {
}
