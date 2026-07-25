package com.reseau_partage.animaux.dto.pesee;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record PeseeResponse(
        Long id,
        Long animalId,
        Long bandeId,
        LocalDate datePesee,
        Integer ageJoursAuMomentPesee,
        BigDecimal poidsKg,
        BigDecimal gainDepuisDernierePeseeKg,
        BigDecimal gmqG,
        BigDecimal ecartCourbeReferencePct,
        Boolean sousPerformeur,
        String operateurNom,
        LocalDateTime dateCreation
) {
}
