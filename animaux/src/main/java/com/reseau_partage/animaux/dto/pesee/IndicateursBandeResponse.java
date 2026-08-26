package com.reseau_partage.animaux.dto.pesee;

import java.math.BigDecimal;
import java.util.List;

public record IndicateursBandeResponse(
        BigDecimal poidsMoyen,
        BigDecimal ecartType,
        BigDecimal cvPct,
        Long nbSousPerformeurs,
        Integer effectifActuel
) {
}
