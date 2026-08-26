package com.reseau_partage.animaux.dto.pesee;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** Agrégats d'une tranche de période (semaine, mois, trimestre, semestre ou année). */
public record BilanTrancheResponse(
        String cle,
        String libelle,
        LocalDate dateDebut,
        LocalDate dateFin,
        Long nbPesees,
        Long nbBandes,
        BigDecimal poidsMoyenKg,
        BigDecimal poidsMinKg,
        BigDecimal poidsMaxKg,
        BigDecimal gmqMoyenG,
        List<BilanBandeItem> bandes
) {}
