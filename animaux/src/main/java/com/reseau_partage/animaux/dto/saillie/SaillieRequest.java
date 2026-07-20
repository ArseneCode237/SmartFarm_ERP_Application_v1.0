package com.reseau_partage.animaux.dto.saillie;

import java.time.LocalDate;

import com.reseau_partage.core.entities.TypeSaillie;

import jakarta.validation.constraints.NotNull;

public record SaillieRequest(
        @NotNull Long truieId,
        Long verratId,                  // null si insémination artificielle
        @NotNull TypeSaillie typeSaillie,
        @NotNull LocalDate dateSaillie,
        LocalDate dateDeuxiemeSaillie,  // confirmation 12h après
        String semenceFournisseur,      // si IA
        String semenceReference,        // numéro de lot semence
        String operateurNom,
        String notes
) {}
