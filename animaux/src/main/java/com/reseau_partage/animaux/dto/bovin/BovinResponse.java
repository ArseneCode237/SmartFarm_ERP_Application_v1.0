package com.reseau_partage.animaux.dto.bovin;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.reseau_partage.core.entities.CategorieBovine;
import com.reseau_partage.core.entities.Sexe;
import com.reseau_partage.core.entities.StatutAnimal;
import com.reseau_partage.core.entities.StatutReproductifBovin;

public record BovinResponse(
        Long id,
        String codeUnique,
        String codeBoucle,
        String codeRfid,
        String nom,
        String race,
        Sexe sexe,
        LocalDate dateNaissance,
        BigDecimal poidsActuelKg,
        StatutAnimal statut,
        Long structureId,
        String structureNom,
        Long mereId,
        Long pereId,
        CategorieBovine categorieBovine,
        StatutReproductifBovin statutReproductif,
        Integer numeroLactationActuelle,
        Integer nbLactationsTotal,
        Integer nbVeauxTotal,
        LocalDate dateVelagePrevu,
        LocalDate dateTarissementPrevu,
        LocalDate dateDebutLactationActuelle,
        BigDecimal productionLaitCumulKg,
        BigDecimal moyenneLait305jKg,
        LocalDateTime dateCreation) {
}
