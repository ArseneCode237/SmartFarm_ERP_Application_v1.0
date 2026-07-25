package com.reseau_partage.animaux.dto.animal;

import com.reseau_partage.core.entities.Espece;
import com.reseau_partage.core.entities.ModeSuivi;
import com.reseau_partage.core.entities.Provenance;
import com.reseau_partage.core.entities.Sexe;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AnimalRequest(
        String codeRfid,
        String codeBoucle,
        @NotNull Espece espece,
        String race,
        String souche,
        Sexe sexe,
        LocalDate dateNaissance,
        @NotNull LocalDate dateEntree,
        @DecimalMin("0.0") BigDecimal poidsEntreeKg,
        @NotNull ModeSuivi modeSuivi,
        Long bandeId,
        @NotNull Long structureId,
        Long mereId,
        Long pereId,
        Provenance provenance,
        String fournisseurNom,
        String numeroLotAchat,
        LocalDate dateDerniereDeclaration,
        String notes
) {
}
