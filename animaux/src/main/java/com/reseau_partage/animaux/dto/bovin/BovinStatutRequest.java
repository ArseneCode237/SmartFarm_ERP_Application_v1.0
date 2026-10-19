package com.reseau_partage.animaux.dto.bovin;

import com.reseau_partage.core.entities.StatutReproductifBovin;

import jakarta.validation.constraints.NotNull;

public record BovinStatutRequest(@NotNull StatutReproductifBovin statut) {
}
