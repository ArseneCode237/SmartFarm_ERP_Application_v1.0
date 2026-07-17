package com.reseau_partage.animaux.dto.config;

import java.math.BigDecimal;

public record CourbeCroissanceRequest(
        Long configEspeceId,
        String race,
        Integer ageJours,
        BigDecimal poidsCibleKg,
        BigDecimal poidsMiniKg,
        BigDecimal poidsMaxiKg
) {
}
