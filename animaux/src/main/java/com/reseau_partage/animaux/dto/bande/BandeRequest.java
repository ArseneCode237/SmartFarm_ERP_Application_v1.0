package com.reseau_partage.animaux.dto.bande;

import com.reseau_partage.core.entities.Categorie;
import com.reseau_partage.core.entities.Espece;
import com.reseau_partage.core.entities.Provenance;
import com.reseau_partage.core.entities.TypeProduction;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BandeRequest(
        @NotNull String nom,
        @NotNull Espece espece,
        String race,
        String souche,
        Categorie categorie,
        @NotNull TypeProduction typeProduction,
        Long siteId,
        @NotNull Long structureId,
        @NotNull Provenance provenance,
        String fournisseurNom,
        BigDecimal coutAchatUnitaire,
        @NotNull Integer effectifInitial,
        LocalDate dateEntree,
        LocalDate dateSortiePrevue,
        BigDecimal poidsMoyenEntreeKg,
        BigDecimal rationJournaliereKg,
        String description,
        String notes
) {
}
