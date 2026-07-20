package com.reseau_partage.animaux.dto.misebas;

import com.reseau_partage.core.entities.TypeMiseBas;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record MiseBaResponse(
        Long id,
        Long saillieId,
        Long truieId,
        String truieCode,
        Integer numeroPortee,
        LocalDateTime dateMiseBasReelle,
        Integer dureeMiseBaMinutes,
        TypeMiseBas typeMiseBas,

        // Résultats
        Integer nbNesVivants,
        Integer nbMortNes,
        Integer nbMomifies,
        BigDecimal poidsMoyenNaissanceKg,
        BigDecimal poidsMinNaissanceKg,
        BigDecimal poidsMaxNaissanceKg,
        Integer nbPorceletsAllaites,

        // Sevrage
        LocalDate dateSevragePrevu,
        LocalDate dateSevrageReel,
        Integer nbSevres,
        BigDecimal poidsMoyenSevrageKg,
        Integer dureeLactationJours,

        // Destination
        Long bandeDestinationId,
        String bandeDestinationNom,

        Integer joursdepuisPorteePrecedente,
        String veterinaireNom,
        String operateurNom,
        String notes,
        LocalDateTime dateCreation
) {}
