package com.reseau_partage.vaccination.dto.plan;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.reseau_partage.core.entities.Espece;
import com.reseau_partage.core.entities.TypeProduction;
import com.reseau_partage.core.entities.enumtypes.VoieAdministration;

public record PlanVaccinationResponse(
        Long id,
        String nom,
        Long fermeId,
        Espece espece,
        TypeProduction typeProduction,
        Boolean actif,
        List<EtapeResponse> etapes,
        LocalDateTime dateCreation) {
    public record EtapeResponse(Long id, Long vaccinId, String vaccinNom, Integer ordreEtape,
            Integer ageCibleJours, Integer toleranceJours, BigDecimal doseMl,
            VoieAdministration voieAdministration, String instructions) {
    }
}
