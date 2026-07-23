package com.reseau_partage.animaux.dto.bande;

import com.reseau_partage.core.entities.Categorie;
import com.reseau_partage.core.entities.Espece;
import com.reseau_partage.core.entities.Provenance;
import com.reseau_partage.core.entities.StatutBande;
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
        Integer effectifActuel,
        Integer effectifMorts,
        Integer effectifVendus,
        Integer effectifReformes,
        Integer totalDeclaresMorts,
        Integer totalDeclaresVendus,
        Integer totalDeclaresReformes,
        BigDecimal revenuTotalVentes,
        LocalDate dateDerniereDeclaration,
        LocalDate dateEntree,
        LocalDate dateSortiePrevue,
        LocalDate dateSortieReelle,
        BigDecimal poidsMoyenEntreeKg,
        BigDecimal poidsMoyenActuelKg,
        BigDecimal poidsTotalSortie,
        BigDecimal rationJournaliereKg,
        BigDecimal fcrCumule,
        BigDecimal tauxPontePct,
        BigDecimal gainMoyenQuotidienG,
        String description,
        StatutBande statut,
        String notes
) {
}
