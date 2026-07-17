package com.reseau_partage.animaux.mapper;

import com.reseau_partage.animaux.dto.animal.AnimalResponse;
import com.reseau_partage.core.entities.Animal;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AnimalMapper {

    AnimalResponse toResponse(Animal entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "codeUnique", ignore = true)
    @Mapping(target = "statut", ignore = true)
    @Mapping(target = "dateCreation", ignore = true)
    @Mapping(target = "dateModification", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Animal toEntity(com.reseau_partage.animaux.dto.animal.AnimalRequest request);
}
