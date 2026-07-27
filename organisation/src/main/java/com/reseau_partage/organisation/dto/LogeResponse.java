package com.reseau_partage.organisation.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record LogeResponse(
        Long id,
        String code,
        String nom,
        String description,
        Integer capaciteMaxAnimaux,
        BigDecimal superficieM2,
        Long batimentId,
        String batimentNom,
        Long siteId,
        String siteNom,
        Long fermeId,
        String fermeNom,
        Long bandeId,
        String bandeNom,
        Integer bandeEffectifActuel,
        Integer animauxAffectesCount,
        List<Map<String, Object>> animauxAffectes,
        Double tauxOccupation,
        LocalDateTime dateCreation,
        LocalDateTime dateModification
) {}
