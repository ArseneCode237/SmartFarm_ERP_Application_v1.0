package com.reseau_partage.organisation.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record SiteRequest(@NotNull Long fermeId, @NotBlank String nom, String adresse, String ville, String region,
        BigDecimal latitude, BigDecimal longitude, @DecimalMin("0.0") BigDecimal superficie, String responsableNom,
        String responsableTelephone) {
}
