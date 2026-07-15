package com.reseau_partage.organisation.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record FermeRequest(@NotBlank String nom, @NotBlank String pays, String devise, String fuseauHoraire,
        @DecimalMin("0.0") BigDecimal superficieTotale, String logoUrl, String telephoneContact,
        @Email String emailContact) {
}
