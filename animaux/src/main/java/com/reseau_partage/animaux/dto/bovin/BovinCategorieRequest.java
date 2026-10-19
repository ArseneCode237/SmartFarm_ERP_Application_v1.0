package com.reseau_partage.animaux.dto.bovin;

import com.reseau_partage.core.entities.CategorieBovine;

import jakarta.validation.constraints.NotNull;

public record BovinCategorieRequest(@NotNull CategorieBovine categorie) {
}
