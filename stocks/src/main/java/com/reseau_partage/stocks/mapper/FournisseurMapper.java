package com.reseau_partage.stocks.mapper;

import com.reseau_partage.stocks.dto.fournisseur.FournisseurResponse;
import com.reseau_partage.core.entities.Fournisseur;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "default", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FournisseurMapper {

    FournisseurResponse toResponse(Fournisseur entity);
}
