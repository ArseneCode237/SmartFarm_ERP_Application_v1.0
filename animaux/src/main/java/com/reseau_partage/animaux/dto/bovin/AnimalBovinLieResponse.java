package com.reseau_partage.animaux.dto.bovin;

import java.time.LocalDate;

import com.reseau_partage.core.entities.Sexe;

public record AnimalBovinLieResponse(
        Long id,
        String codeUnique,
        String nom,
        String race,
        Sexe sexe,
        LocalDate dateNaissance) {
}
