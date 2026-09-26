package com.reseau_partage.vaccination.dto.vaccin;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.reseau_partage.core.entities.Espece;
import com.reseau_partage.core.entities.enumtypes.TypeVaccin;
import com.reseau_partage.core.entities.enumtypes.VoieAdministration;

public record VaccinResponse(
        Long id,
        String nom,
        String fabricant,
        String numeroAmm,
        TypeVaccin typeVaccin,
        List<Espece> especesCompatibles,
        String maladiesCiblees,
        VoieAdministration voieAdministration,
        BigDecimal doseMl,
        Integer agePremiereDoseJours,
        Integer intervalleRappelJours,
        Integer nombreDosesProtocole,
        Integer delaiAttenteAbattageJours,
        Integer temperatureConservationMin,
        Integer temperatureConservationMax,
        Integer dureeValiditeApresOuvertureHeures,
        Boolean actif,
        String notes,
        LocalDateTime dateCreation) {
}
