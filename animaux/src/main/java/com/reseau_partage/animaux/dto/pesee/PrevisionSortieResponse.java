package com.reseau_partage.animaux.dto.pesee;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PrevisionSortieResponse(
        LocalDate datePrevueSortie,
        BigDecimal poidsPrevuKg,
        Long margeJours
) {
}
