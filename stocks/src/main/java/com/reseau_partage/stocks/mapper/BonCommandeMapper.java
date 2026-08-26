package com.reseau_partage.stocks.mapper;

import com.reseau_partage.stocks.dto.boncommande.BonCommandeResponse;
import com.reseau_partage.core.entities.BonCommande;
import com.reseau_partage.core.entities.LigneBonCommande;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "default", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BonCommandeMapper {

    @Mapping(target = "fournisseurId", ignore = true)
    @Mapping(target = "fournisseurNom", ignore = true)
    @Mapping(target = "lignes", expression = "java(mapLignes(entity.getLignes()))")
    BonCommandeResponse toResponse(BonCommande entity);

    /**
     * Mappe les lignes vers un DTO sérialisable (pas d'entités JPA ni de
     * proxies Hibernate dans la réponse JSON).
     */
    default List<com.reseau_partage.stocks.dto.boncommande.LigneBonCommandeResponse> mapLignes(List<LigneBonCommande> lignes) {
        if (lignes == null) return List.of();
        return lignes.stream().map(l -> {
            com.reseau_partage.stocks.dto.boncommande.LigneBonCommandeResponse r =
                    new com.reseau_partage.stocks.dto.boncommande.LigneBonCommandeResponse();
            r.setId(l.getId());
            if (l.getArticle() != null) {
                r.setArticleId(l.getArticle().getId());
                r.setArticleNom(l.getArticle().getDesignation());
                r.setArticleReference(l.getArticle().getCodeArticle());
                r.setUniteMesure(l.getArticle().getUniteMesure() != null
                        ? l.getArticle().getUniteMesure().name() : null);
            }
            r.setQuantiteCommandee(l.getQuantiteCommandee());
            r.setQuantiteRecue(l.getQuantiteRecue());
            r.setPrixUnitaire(l.getPrixUnitaire());
            r.setMontantLigne(l.getMontantLigne());
            return r;
        }).toList();
    }
}
