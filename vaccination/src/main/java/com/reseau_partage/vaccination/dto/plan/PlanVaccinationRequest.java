package com.reseau_partage.vaccination.dto.plan;

import java.util.List;

import com.reseau_partage.core.entities.Espece;
import com.reseau_partage.core.entities.TypeProduction;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record PlanVaccinationRequest(
        @NotBlank String nom,
        @NotNull Long fermeId,
        @NotNull Espece espece,
        TypeProduction typeProduction,
        @NotEmpty List<@Valid EtapePlanRequest> etapes) {
}
