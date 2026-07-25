package com.reseau_partage.animaux.dto.declaration;

import com.reseau_partage.core.entities.MotifDeclaration;
import com.reseau_partage.core.entities.TypeDeclaration;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DeclarationAnimalRequest(
        @NotNull Long animalId,
        @NotNull TypeDeclaration type,
        @NotNull MotifDeclaration motif,
        @NotNull @PastOrPresent LocalDate dateDeclaration,
        @DecimalMin("0.001") BigDecimal poidsKg,
        Boolean prixParKg,
        @DecimalMin("0.01") BigDecimal prixUnitaire,
        @Size(max = 255) String nomAcheteur,
        @Size(max = 20) String telephoneAcheteur,
        @Size(max = 150) String localiteAcheteur,
        String observations
) {
}
