package com.reseau_partage.animaux.dto.declaration;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.reseau_partage.core.entities.MotifDeclaration;
import com.reseau_partage.core.entities.TypeDeclaration;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

public record DeclarationAnimalRequest(
        @NotNull Long animalId,
        @NotNull TypeDeclaration type,
        @NotNull MotifDeclaration motif,
        @NotNull @PastOrPresent LocalDate dateDeclaration,
        /** Poids de l'animal en kg. */
        @DecimalMin("0.001") BigDecimal poidsKg,
        /** Poids total de la vente en kg (optionnel — calculé automatiquement depuis poidsKg si absent). */
        @DecimalMin("0.001") BigDecimal poidsTotalKg,
        /** Poids moyen par animal (optionnel — calculé automatiquement). */
        @DecimalMin("0.001") BigDecimal poidsMoyenKg,
        Boolean prixParKg,
        @DecimalMin("0.01") BigDecimal prixUnitaire,
        @Size(max = 255) String nomAcheteur,
        @Size(max = 20) String telephoneAcheteur,
        @Size(max = 150) String localiteAcheteur,
        String observations
) {
}
