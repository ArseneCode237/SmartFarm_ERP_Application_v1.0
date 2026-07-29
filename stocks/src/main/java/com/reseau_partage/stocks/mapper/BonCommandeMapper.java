package com.reseau_partage.stocks.mapper;

import com.reseau_partage.stocks.dto.boncommande.BonCommandeResponse;
import com.reseau_partage.core.entities.BonCommande;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BonCommandeMapper {

    @Mapping(target = "fournisseurId", ignore = true)
    @Mapping(target = "fournisseurNom", ignore = true)
    BonCommandeResponse toResponse(BonCommande entity);
}