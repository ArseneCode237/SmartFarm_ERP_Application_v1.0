package com.reseau_partage.animaux.dto.porcin;

import java.util.List;

public record ExtractionBandeResponse(
        Long bandeId,
        String bandeNom,
        Integer nbExtraits,
        Integer effectifRestant,
        List<PorcinResponse> animauxCrees
) {}
