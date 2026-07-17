package com.reseau_partage.animaux.mapper;

import com.reseau_partage.animaux.dto.pesee.PeseeResponse;
import com.reseau_partage.core.entities.Pesee;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class PeseeMapper {

    public PeseeResponse toResponse(Pesee p) {
        return new PeseeResponse(
                p.getId(),
                p.getAnimal() != null ? p.getAnimal().getId() : null,
                p.getBande() != null ? p.getBande().getId() : null,
                p.getDatePesee(),
                p.getAgeJoursAuMomentPesee(),
                p.getPoidsKg(),
                p.getGainDepuisDernierePeseeKg(),
                p.getGmqG(),
                p.getEcartCourbeReferencePct(),
                p.getSousPerformeur(),
                p.getOperateurNom(),
                p.getDateCreation()
        );
    }
}
