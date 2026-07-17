package com.reseau_partage.animaux.dto.config;

import com.reseau_partage.core.entities.Espece;

public record TypeEvenementCustomRequest(
        Espece espece,
        String libelle,
        String description,
        Boolean actif
) {
}
