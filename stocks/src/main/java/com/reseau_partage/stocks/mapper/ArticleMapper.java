package com.reseau_partage.stocks.mapper;

import com.reseau_partage.stocks.dto.article.ArticleResponse;
import com.reseau_partage.core.entities.Article;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ArticleMapper {

    ArticleResponse toResponse(Article entity);
}