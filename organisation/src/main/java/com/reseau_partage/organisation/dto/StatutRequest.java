package com.reseau_partage.organisation.dto;

import jakarta.validation.constraints.NotBlank;

public record StatutRequest(@NotBlank String statut) {
}