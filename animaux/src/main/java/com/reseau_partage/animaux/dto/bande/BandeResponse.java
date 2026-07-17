package com.reseau_partage.animaux.dto.bande;

import com.reseau_partage.core.entities.Categorie;
import com.reseau_partage.core.entities.Espece;
import com.reseau_partage.core.entities.Provenance;
import com.reseau_partage.core.entities.StatutBande;
import com.reseau_partage.core.entities.TypeProduction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record BandeResponse(
        Long id,
        String codeBande,
        String nom,
        Espece espece,
        String race,
        String souche,
        Categorie categorie,
        TypeProduction typeProduction,
        Long siteId,
        String siteNom,
        Long structureId,
        String structureNom,
        Provenance provenance,
        String fournisseurNom,
        BigDecimal coutAchatUnitaire,
        Integer effectifInitial,
        Integer effectifActuel,
        Integer effectifMorts,
        Integer effectifVendus,
        Integer effectifReformes,
        Double tauxMortalitePct,
        LocalDate dateEntree,
        LocalDate dateSortiePrevue,
        LocalDate dateSortieReelle,
        Integer ageMoyenJours,
        BigDecimal poidsMoyenEntreeKg,
        BigDecimal poidsTotalSortie,
        BigDecimal poidsMoyenActuelKg,
        BigDecimal fcrCumule,
        BigDecimal tauxPontePct,
        BigDecimal gainMoyenQuotidienG,
        BigDecimal rationJournaliereKg,
        String description,
        StatutBande statut,
        String notes,
        LocalDateTime dateCreation,
        LocalDateTime dateModification
) {
}
