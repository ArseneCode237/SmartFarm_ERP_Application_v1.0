package com.reseau_partage.animaux.dto.bovin;

import java.time.LocalDateTime;
import java.util.List;

import com.reseau_partage.core.entities.CategorieBovine;
import com.reseau_partage.core.entities.TypeVelage;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record VelageBovinRequest(
        @NotNull Long saillieId,
        @NotNull LocalDateTime dateVelageReel,
        @NotNull TypeVelage typeVelage,
        @NotNull List<@Valid VeauNouveauNeRequest> veauxVivants,
        @NotNull @Min(0) Integer nbVeauxMortNes,
        CategorieBovine categoriePostVelage,
        Boolean retentionPlacenta,
        Boolean fievreDeLait,
        String veterinaireNom,
        String operateurNom,
        String notes) {
}
