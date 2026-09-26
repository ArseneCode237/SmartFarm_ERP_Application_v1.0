package com.reseau_partage.vaccination.dto.bande;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.reseau_partage.core.entities.Espece;
import com.reseau_partage.core.entities.enumtypes.StatutVaccination;
import com.reseau_partage.core.entities.enumtypes.TypeVaccination;
import com.reseau_partage.core.entities.enumtypes.VoieAdministration;

public record VaccinationBandeResponse(
        Long id, Long bandeId, String bandeNom, Espece espece, Long fermeId,
        Long vaccinId, String vaccinNom, String numeroLotVaccin, LocalDate dateExpirationLot,
        TypeVaccination typeVaccination, Integer numeroDoseDansProtocole, LocalDate dateVaccination,
        Integer ageBandeJoursAuMoment, VoieAdministration voieAdministration,
        BigDecimal doseMlParTete, Integer nbAnimauxVaccines, BigDecimal doseTotaleMl,
        StatutVaccination statut, LocalDate dateProchaineRappel, Long planVaccinationId,
        Long etapePlanId, LocalDate dateFinDelaiAttente, String veterinaireNom,
        String operateurNom, String notes, LocalDateTime dateCreation) {
}
