package com.reseau_partage.animaux.dto.reproduction;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SevrageRequest(
        LocalDate dateSevrageReel,
        BigDecimal poidsAuSevrageKg,
        String notes
) {
}
