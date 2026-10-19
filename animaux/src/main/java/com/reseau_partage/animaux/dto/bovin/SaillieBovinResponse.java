package com.reseau_partage.animaux.dto.bovin;

import java.time.LocalDate;

import com.reseau_partage.core.entities.StatutSaillie;
import com.reseau_partage.core.entities.TypeSaillie;

public record SaillieBovinResponse(
        Long id,
        Long vacheId,
        Long taureauId,
        TypeSaillie typeSaillie,
        LocalDate dateSaillie,
        StatutSaillie statut,
        LocalDate dateConfirmationEcho,
        LocalDate dateVelagePrevu,
        LocalDate datePreVelagePrevue,
        LocalDate dateTarissementPrevue,
        String motifEchec) {
}
