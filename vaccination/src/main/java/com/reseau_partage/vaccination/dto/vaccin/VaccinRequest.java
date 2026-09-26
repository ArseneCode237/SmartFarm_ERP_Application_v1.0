package com.reseau_partage.vaccination.dto.vaccin;

import java.math.BigDecimal;
import java.util.List;

import com.reseau_partage.core.entities.Espece;
import com.reseau_partage.core.entities.enumtypes.TypeVaccin;
import com.reseau_partage.core.entities.enumtypes.VoieAdministration;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

public record VaccinRequest(
        @NotBlank String nom,
        String fabricant,
        String numeroAmm,
        TypeVaccin typeVaccin,
        List<Espece> especesCompatibles,
        String maladiesCiblees,
        VoieAdministration voieAdministration,
        @DecimalMin(value = "0", inclusive = false) BigDecimal doseMl,
        Integer agePremiereDoseJours,
        Integer intervalleRappelJours,
        Integer nombreDosesProtocole,
        Integer delaiAttenteAbattageJours,
        Integer temperatureConservationMin,
        Integer temperatureConservationMax,
        Integer dureeValiditeApresOuvertureHeures,
        String notes) {
}
