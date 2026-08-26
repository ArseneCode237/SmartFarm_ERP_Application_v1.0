package com.reseau_partage.animaux.mapper;

import com.reseau_partage.animaux.dto.reproduction.EvenementReproductionResponse;
import com.reseau_partage.core.entities.Animal;
import com.reseau_partage.core.entities.EvenementReproduction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class EvenementReproductionMapper {

    public EvenementReproductionResponse toResponse(EvenementReproduction e) {
        Animal femelle = e.getFemelle();
        Animal male = e.getMale();
        return new EvenementReproductionResponse(
                e.getId(),
                femelle != null ? femelle.getId() : null,
                femelle != null ? femelle.getCodeUnique() : null,
                male != null ? male.getId() : null,
                male != null ? male.getCodeUnique() : null,
                e.getType(),
                e.getDateSaillie(),
                e.getDateMiseBasPrevue(),
                e.getDateMiseBasReelle(),
                e.getStatut(),
                e.getNombreNesVivants(),
                e.getNombreNesMorts(),
                e.getPoidsMoyenNaissanceKg(),
                e.getDateSevragePrevu(),
                e.getDateSevrageReel(),
                e.getPoidsAuSevrageKg(),
                e.getNotes(),
                e.getDateCreation()
        );
    }
}
