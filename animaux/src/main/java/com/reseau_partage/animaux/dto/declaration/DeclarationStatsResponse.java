package com.reseau_partage.animaux.dto.declaration;

import java.math.BigDecimal;
import java.util.List;

public record DeclarationStatsResponse(
        Long bandeId,
        String bandeNom,
        Integer effectifInitial,
        Integer effectifActuel,
        Integer totalMorts,
        Integer totalVendus,
        Integer totalReformes,
        BigDecimal revenuTotalVentes,
        Double tauxMortalitePct,
        Double tauxVentePct,
        List<PointCourbe> courbeMortalite,
        List<PointCourbe> courbeEffectif,
        List<PointCamembert> mortaliteParMotif
) {
    public record PointCourbe(String date, Integer valeur, Integer effectifCumule) {}
    public record PointCamembert(String motif, Integer quantite, Double pct) {}
}
