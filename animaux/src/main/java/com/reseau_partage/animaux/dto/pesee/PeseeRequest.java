package com.reseau_partage.animaux.dto.pesee;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PeseeRequest(
        Long animalId,
        Long bandeId,
        LocalDate datePesee,
        BigDecimal poidsKg,
        String operateurNom,
        String notes
) {
}
