package com.reseau_partage.stocks.config;

import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.reseau_partage.stocks.mapper.BonCommandeMapper;
import com.reseau_partage.stocks.mapper.FournisseurMapper;

/**
 * Enregistre explicitement les implementations MapStruct comme beans Spring.
 * Cela evite de dependre de la detection automatique des classes generees.
 */
@Configuration
public class MapperConfig {

    @Bean
    public BonCommandeMapper bonCommandeMapper() {
        return Mappers.getMapper(BonCommandeMapper.class);
    }

    @Bean
    public FournisseurMapper fournisseurMapper() {
        return Mappers.getMapper(FournisseurMapper.class);
    }
}
