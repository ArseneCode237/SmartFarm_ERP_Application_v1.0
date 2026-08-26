package com.reseau_partage.animaux.dto.pesee;

import java.util.List;

/**
 * Bilan complet des pesées agrégé par période.
 * periode : SEMAINE | MOIS | TRIMESTRE | SEMESTRE | ANNUEL
 */
public record BilanPeseesResponse(
        String periode,
        Integer annee,
        Long totalPesees,
        List<BilanTrancheResponse> tranches
) {}
