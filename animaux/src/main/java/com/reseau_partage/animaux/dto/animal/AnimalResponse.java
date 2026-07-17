package com.reseau_partage.animaux.dto.animal;

import com.reseau_partage.core.entities.Espece;
import com.reseau_partage.core.entities.ModeSuivi;
import com.reseau_partage.core.entities.Provenance;
import com.reseau_partage.core.entities.Sexe;
import com.reseau_partage.core.entities.StatutAnimal;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record AnimalResponse(
        Long id,
        String codeUnique,
        String codeRfid,
        String codeBoucle,
        Espece espece,
        String race,
        String souche,
        Sexe sexe,
        LocalDate dateNaissance,
        LocalDate dateEntree,
        Integer ageJours,
        BigDecimal poidsEntreeKg,
        BigDecimal poidsActuelKg,
        BigDecimal gainTotalKg,
        LocalDate dateDernierePesee,
        ModeSuivi modeSuivi,
        Long bandeId,
        String bandeNom,
        Long structureId,
        String structureNom,
        String siteNom,
        Long mereId,
        String mereCode,
        Long pereId,
        String pereCode,
        Provenance provenance,
        String fournisseurNom,
        StatutAnimal statut,
        LocalDate dateSortie,
        String motifSortie,
        String causeMort,
        String notes,
        LocalDateTime dateCreation,
        LocalDateTime dateModification
) {
}
