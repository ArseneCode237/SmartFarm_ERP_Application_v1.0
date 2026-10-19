package com.reseau_partage.animaux.dto.bovin;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ControleLaitierResponse(
        Long id,
        Long animalId,
        LocalDate dateControle,
        Integer numeroLactation,
        Integer joursEnLait,
        BigDecimal laitMatinKg,
        BigDecimal laitMidiKg,
        BigDecimal laitSoirKg,
        BigDecimal laitTotalJourKg,
        BigDecimal laitCibleJourKg,
        BigDecimal ecartVsCiblePct,
        BigDecimal laitCumulLactationKg,
        String typeAlerte) {
}
