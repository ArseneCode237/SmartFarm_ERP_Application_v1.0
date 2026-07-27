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
        // ============================================
        // CHAMPS EXISTANTS
        // ============================================
        
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
        String notes,

        // ============================================
        // 🔥 NOUVEAUX CHAMPS SPÉCIFIQUES AUX POISSONS
        // ============================================
        
        // Origine (pour NAISSANCE_INTERNE)
        Long mereId,
        Long pereId,
        
        // Paramètres aquacoles
        BigDecimal densitePoissons,
        BigDecimal tailleMoyenne,
        com.reseau_partage.core.entities.AlimentationPoisson alimentation,
        com.reseau_partage.core.entities.SystemeElevagePoisson systemeElevage,
        BigDecimal temperatureEau,
        BigDecimal phEau,
        BigDecimal oxygeneDissous,
        String racePoisson
) {
    /**
     * Vérifie si la requête concerne des poissons
     */
    public boolean isPoisson() {
        return espece != null && espece == Espece.POISSON;
    }

    /**
     * Vérifie si la provenance est NAISSANCE_INTERNE
     */
    public boolean isNaissanceInterne() {
        return provenance != null && provenance == Provenance.NAISSANCE_INTERNE;
    }

    /**
     * Vérifie si la provenance est ACHAT_EXTERNE
     */
    public boolean isAchatExterne() {
        return provenance != null && provenance == Provenance.ACHAT_EXTERNE;
    }

    /**
     * Récupère la catégorie appropriée selon l'espèce
     */
    public Categorie getEffectiveCategorie() {
        if (categorie != null) {
            return categorie;
        }
        // Valeur par défaut selon l'espèce
        return isPoisson() ? Categorie.JUVENILE : Categorie.PORCELET;
    }
}