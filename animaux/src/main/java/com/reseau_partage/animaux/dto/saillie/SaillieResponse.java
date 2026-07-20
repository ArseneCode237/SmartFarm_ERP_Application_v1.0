package com.reseau_partage.animaux.dto.saillie;

import com.reseau_partage.core.entities.StatutSaillie;
import com.reseau_partage.core.entities.TypeSaillie;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record SaillieResponse(
        Long id,
        Long truieId,
        String truieCode,
        Long verratId,
        String verratCode,
        TypeSaillie typeSaillie,
        Integer numeroSaillieCarriere,
        Integer numeroPorteeCorrespondante,
        LocalDate dateSaillie,
        LocalDate dateDeuxiemeSaillie,
        StatutSaillie statut,
        LocalDate dateConfirmationEcho,
        LocalDate dateInfirmation,
        String motifEchec,
        LocalDate dateMiseBasPrevue,
        LocalDate dateTransfertMaternitePrevue,
        String semenceFournisseur,
        String operateurNom,
        String notes,
        LocalDateTime dateCreation
) {}
