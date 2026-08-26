package com.reseau_partage.animaux.dto.declaration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.reseau_partage.core.entities.MotifDeclaration;
import com.reseau_partage.core.entities.SourceDeclaration;
import com.reseau_partage.core.entities.StatutDeclaration;
import com.reseau_partage.core.entities.TypeDeclaration;

public record DeclarationAnimalResponse(
        Long id,
        Long animalId,
        String animalCodeUnique,
        String animalCodeBoucle,
        String espece,
        Long fermeId,
        TypeDeclaration type,
        MotifDeclaration motif,
        LocalDate dateDeclaration,
        BigDecimal poidsKg,
        BigDecimal poidsTotalKg,
        BigDecimal poidsMoyenKg,
        Boolean prixParKg,
        BigDecimal prixUnitaire,
        BigDecimal montantTotal,
        String nomAcheteur,
        String telephoneAcheteur,
        String localiteAcheteur,
        String observations,
        SourceDeclaration source,
        StatutDeclaration statut,
        String utilisateurNom,
        LocalDateTime dateCreation,
        LocalDateTime dateModification
) {
}
