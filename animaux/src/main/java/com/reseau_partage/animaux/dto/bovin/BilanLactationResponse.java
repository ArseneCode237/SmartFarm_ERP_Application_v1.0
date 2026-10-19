package com.reseau_partage.animaux.dto.bovin;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BilanLactationResponse(
        Long animalId,
        Integer numeroLactation,
        BigDecimal productionMesureeKg,
        BigDecimal productionEstimee305jKg,
        BigDecimal picJournalierKg,
        LocalDate datePic,
        BigDecimal persistancePct,
        Integer nombreControles) {
}
