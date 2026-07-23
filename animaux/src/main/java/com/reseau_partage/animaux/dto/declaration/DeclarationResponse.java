package com.reseau_partage.animaux.dto.declaration;

import com.reseau_partage.core.entities.MotifDeclaration;
import com.reseau_partage.core.entities.SourceDeclaration;
import com.reseau_partage.core.entities.StatutDeclaration;
import com.reseau_partage.core.entities.TypeDeclaration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record DeclarationResponse(
        Long id,
        Long bandeId,
        String bandeNom,
        String espece,
        Long fermeId,
        TypeDeclaration type,
        MotifDeclaration motif,
        LocalDate dateDeclaration,
        Integer quantite,
        Integer effectifAvantDeclaration,
        Integer effectifApresDeclaration,
        BigDecimal poidsMoyenKg,
        BigDecimal poidsTotalKg,
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
