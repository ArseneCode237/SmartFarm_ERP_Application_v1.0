package com.reseau_partage.vaccination.dto.individu;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.reseau_partage.core.entities.enumtypes.TypeVaccination;
import com.reseau_partage.core.entities.enumtypes.VoieAdministration;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record VaccinationIndividuelleRequest(
        @NotNull Long animalId,
        @NotNull Long vaccinId,
        String numeroLotVaccin,
        LocalDate dateExpirationLot,
        TypeVaccination typeVaccination,
        Integer numeroDoseDansProtocole,
        LocalDate dateVaccination,
        VoieAdministration voieAdministration,
        @DecimalMin(value = "0", inclusive = false) BigDecimal doseMl,
        String reactionObservee,
        String veterinaireNom,
        String operateurNom,
        String notes) {
}
