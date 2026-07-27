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
        // ============================================
        // CHAMPS EXISTANTS
        // ============================================
        
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
        Integer totalDeclaresMorts,
        Integer totalDeclaresVendus,
        Integer totalDeclaresReformes,
        BigDecimal revenuTotalVentes,
        LocalDate dateDerniereDeclaration,
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
        LocalDateTime dateModification,

        // ============================================
        // 🔥 NOUVEAUX CHAMPS SPÉCIFIQUES AUX POISSONS
        // ============================================
        
        // Origine (pour NAISSANCE_INTERNE)
        Long mereId,
        String mereNom,
        String mereNumeroIdentification,
        Long pereId,
        String pereNom,
        String pereNumeroIdentification,
        
        // Paramètres aquacoles
        BigDecimal densitePoissons,
        BigDecimal tailleMoyenne,
        String alimentation,
        String systemeElevage,
        BigDecimal temperatureEau,
        BigDecimal phEau,
        BigDecimal oxygeneDissous,
        String racePoisson,
        
        // Indicateurs aquacoles (calculés)
        Double tauxSurviePct,
        Double poidsTotalEstimeKg,
        Double biomasseEstimeeKg,
        Double consommationAlimentaireTotaleKg,
        Double tauxConversionAlimentaire,
        Double gainMoyenQuotidien,
        Double densiteOptimalePct
) {
    /**
     * Vérifie si la bande est une bande piscicole
     */
    public boolean isPoisson() {
        return espece != null && espece == Espece.POISSON;
    }

    /**
     * Vérifie si la bande est une bande porcine
     */
    public boolean isPorcin() {
        return espece != null && espece == Espece.PORC;
    }

    /**
     * Récupère le taux de survie
     */
    public Double getTauxSurviePct() {
        if (effectifInitial != null && effectifInitial > 0 && effectifActuel != null) {
            return (effectifActuel.doubleValue() / effectifInitial.doubleValue()) * 100;
        }
        return null;
    }

    /**
     * Récupère la biomasse estimée
     */
    public Double getBiomasseEstimeeKg() {
        if (poidsMoyenActuelKg != null && effectifActuel != null) {
            return poidsMoyenActuelKg.doubleValue() * effectifActuel.doubleValue();
        }
        return null;
    }

    /**
     * Récupère la densité optimale (si densitePoissons et poidsMoyenActuelKg sont renseignés)
     */
    public Double getDensiteOptimalePct() {
        if (densitePoissons != null && poidsMoyenActuelKg != null) {
            // Densité optimale = nombre de poissons par kg
            // Exemple: si densité = 50 poissons/m² et poids moyen = 0.5kg
            // densité optimale = (50 * 0.5) / 1 = 25 kg/m²
            return densitePoissons.doubleValue() * poidsMoyenActuelKg.doubleValue();
        }
        return null;
    }

    /**
     * Vérifie si les paramètres aquacoles sont présents
     */
    public boolean hasAquacultureData() {
        return densitePoissons != null || 
               tailleMoyenne != null || 
               temperatureEau != null || 
               phEau != null || 
               oxygeneDissous != null;
    }

    /**
     * Récupère le type de production adapté selon l'espèce
     */
    public String getProductionAdaptee() {
        if (isPoisson()) {
            return typeProduction != null ? "Aquacole - " + typeProduction : "Aquacole";
        }
        return typeProduction != null ? "Porcin - " + typeProduction : "Porcin";
    }
}