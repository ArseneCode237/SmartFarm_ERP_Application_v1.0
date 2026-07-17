package com.reseau_partage.animaux.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.reseau_partage.animaux.dto.bande.BandeResponse;
import com.reseau_partage.core.entities.Bande;

import com.reseau_partage.core.entities.StatutBande;
import org.mapstruct.BeforeMapping;

@Mapper(componentModel = "spring")
public interface BandeMapper {

    @Mapping(target = "siteId", source = "site.id")
    @Mapping(target = "siteNom", source = "site.nom")
    @Mapping(target = "structureId", source = "structure.id")
    @Mapping(target = "structureNom", source = "structure.nom")
    @Mapping(target = "tauxMortalitePct", ignore = true)
    @Mapping(target = "ageMoyenJours", ignore = true)
    BandeResponse toResponse(Bande entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "codeBande", ignore = true)
    @Mapping(target = "site", ignore = true)
    @Mapping(target = "structure", ignore = true)
    @Mapping(target = "effectifActuel", expression = "java(request.effectifInitial())")
    @Mapping(target = "effectifMorts", ignore = true)
    @Mapping(target = "effectifVendus", ignore = true)
    @Mapping(target = "effectifReformes", ignore = true)
    @Mapping(target = "dateSortieReelle", ignore = true)
    @Mapping(target = "poidsTotalSortie", ignore = true)
    @Mapping(target = "poidsMoyenActuelKg", ignore = true)
    @Mapping(target = "fcrCumule", ignore = true)
    @Mapping(target = "tauxPontePct", ignore = true)
    @Mapping(target = "gainMoyenQuotidienG", ignore = true)
    @Mapping(target = "dateCreation", ignore = true)
    @Mapping(target = "dateModification", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Bande toEntity(com.reseau_partage.animaux.dto.bande.BandeRequest request);

    @BeforeMapping
    default void setDefaultStatut(com.reseau_partage.animaux.dto.bande.BandeRequest request, @org.mapstruct.MappingTarget Bande bande) {
        if (bande.getStatut() == null && (request == null || request.statut() == null)) {
            bande.setStatut(StatutBande.EN_COURS);
        }
    }
}
