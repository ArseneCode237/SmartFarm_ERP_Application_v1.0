package com.reseau_partage.stocks.mapper;

import com.reseau_partage.stocks.dto.mouvement.MouvementResponse;
import com.reseau_partage.core.entities.MouvementStock;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MouvementStockMapper {

    @Mapping(target = "articleId", ignore = true)
    @Mapping(target = "articleDesignation", ignore = true)
    @Mapping(target = "articleCode", ignore = true)
    @Mapping(target = "fournisseurNom", ignore = true)
    MouvementResponse toResponse(MouvementStock entity);
}