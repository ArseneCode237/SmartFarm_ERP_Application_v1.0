package com.reseau_partage.animaux.dto.pesee;

import java.math.BigDecimal;

/** Bilan d'une bande pour une tranche de période donnée. */
public record BilanBandeItem(
        Long bandeId,
        String codeBande,
        String nom,
        Long nbPesees,
        BigDecimal poidsMoyenKg,
        BigDecimal poidsMinKg,
        BigDecimal poidsMaxKg,
        BigDecimal gmqMoyenG
) {}
