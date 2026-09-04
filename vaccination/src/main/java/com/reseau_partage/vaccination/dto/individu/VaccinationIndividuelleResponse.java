package com.reseau_partage.vaccination.dto.individu;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.reseau_partage.core.entities.Espece;
import com.reseau_partage.core.entities.enumtypes.TypeVaccination;
import com.reseau_partage.core.entities.enumtypes.VoieAdministration;

public record VaccinationIndividuelleResponse(
        Long id, Long animalId, String animalCode, Espece espece, Long fermeId,
        Long vaccinId, String vaccinNom, String numeroLotVaccin, LocalDate dateExpirationLot,
        TypeVaccination typeVaccination, Integer numeroDoseDansProtocole, LocalDate dateVaccination,
        Integer ageAnimalJoursAuMoment, VoieAdministration voieAdministration, BigDecimal doseMl,
        LocalDate dateProchaineRappel, LocalDate dateFinDelaiAttente, String reactionObservee,
        String veterinaireNom, String operateurNom, String notes, LocalDateTime dateCreation) {
}
