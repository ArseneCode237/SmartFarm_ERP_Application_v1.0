package com.reseau_partage.animaux.dto.bovin;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.reseau_partage.core.entities.TypeProductionBovin;

/**
 * Spécifique bovin d'une bande (troupeau). Les indicateurs génériques
 * (poids moyen, FCR cumulé, GMQ réel, ration, date de sortie) sont portés par la bande.
 */
public record ProfilBandeBovinRequest(
        TypeProductionBovin typeProductionBovin,
        String race,
        BigDecimal poidsCibleSortieKg,
        Integer ageCibleAbattageMois,
        Integer gmqCibleG,
        BigDecimal fcrCible,
        Integer dureeEngraissementJours,
        LocalDate dateAbattagePrevue,
        BigDecimal rendementCarcassePct) {
}
