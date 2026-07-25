package com.reseau_partage.animaux.dto.config;

import com.reseau_partage.core.entities.Espece;

import java.math.BigDecimal;

public record ConfigEspeceRequest(
        Integer dureeGestationJours,
        Integer ageMaturiteSexuelleJours,
        Integer intervalleEntreGestationsJours,
        Integer dureeSevrageJours,
        Integer taillePorteeMoyenne,
        BigDecimal poidsNaissanceMoyenKg,
        BigDecimal poidsAbattageCibleKg,
        Integer ageCibleAbattageJours,
        BigDecimal fcrCibleMoyen,
        BigDecimal gmqCibleG,
        Integer intervalleVaccinationJours,
        Integer dureeQuarantaineJours,
        Integer seuilAlerteMortalitePct,
        BigDecimal tauxPonteCiblePct,
        Integer dureeProductionLaitJours,
        BigDecimal productionLaitJournaliereLitres
) {
}
