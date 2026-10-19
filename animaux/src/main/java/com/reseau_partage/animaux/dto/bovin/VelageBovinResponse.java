package com.reseau_partage.animaux.dto.bovin;

import java.time.LocalDateTime;
import java.util.List;

import com.reseau_partage.core.entities.TypeVelage;

public record VelageBovinResponse(
        Long id,
        Long vacheId,
        Long saillieId,
        Integer numeroVelage,
        LocalDateTime dateVelageReel,
        TypeVelage typeVelage,
        Integer nbVeauxVivants,
        Integer nbVeauxMortNes,
        List<Long> veauIds,
        Boolean retentionPlacenta,
        Boolean fievreDeLait) {
}
