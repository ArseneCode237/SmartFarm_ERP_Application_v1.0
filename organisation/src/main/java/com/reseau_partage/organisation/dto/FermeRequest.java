package com.reseau_partage.organisation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.reseau_partage.core.util.CommaSeparatedListDeserializer;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.util.List;

public record FermeRequest(
        @NotBlank String nom,
        @NotBlank String pays,
        String devise,
        String fuseauHoraire,
        @DecimalMin("0.0") BigDecimal superficieTotale,
        String logoUrl,
        String telephoneContact,
        @Email String emailContact,
        // localisation : valeur libre, pas de validation métier
        String localisation,
        // type_activite : agriculture, elevage, aviculture, pisciculture
        @JsonProperty("type_activite")
        @JsonDeserialize(using = CommaSeparatedListDeserializer.class)
        List<String> typeActivite,
        // type_service : stock, vaccination, comptabilite, maintenance, videosurveillance
        @JsonProperty("type_service")
        @JsonDeserialize(using = CommaSeparatedListDeserializer.class)
        List<String> typeService
) {}
