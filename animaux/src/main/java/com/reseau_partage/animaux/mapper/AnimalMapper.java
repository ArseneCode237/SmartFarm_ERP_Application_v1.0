package com.reseau_partage.animaux.mapper;

import com.reseau_partage.animaux.dto.animal.AnimalResponse;
import com.reseau_partage.core.entities.Animal;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AnimalMapper {

    @Mapping(target = "structureId", source = "structure.id")
    @Mapping(target = "structureNom", source = "structure.nom")
    @Mapping(target = "siteNom", source = "structure.site.nom")
    @Mapping(target = "bandeId", source = "bande.id")
    @Mapping(target = "bandeNom", source = "bande.nom")
    @Mapping(target = "mereId", source = "mere.id")
    @Mapping(target = "mereCode", source = "mere.codeUnique")
    @Mapping(target = "pereId", source = "pere.id")
    @Mapping(target = "pereCode", source = "pere.codeUnique")
    AnimalResponse toResponse(Animal entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "codeUnique", ignore = true)
    @Mapping(target = "statut", ignore = true)
    @Mapping(target = "dateCreation", ignore = true)
    @Mapping(target = "dateModification", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Animal toEntity(com.reseau_partage.animaux.dto.animal.AnimalRequest request);
}
