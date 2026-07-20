package com.reseau_partage.animaux.dto.saillie;

import java.time.LocalDate;

import com.reseau_partage.core.entities.StatutSaillie;

import jakarta.validation.constraints.NotNull;

/** Confirmer ou infirmer une gestation après échographie à J+28. */
public record SaillieConfirmationRequest(
        @NotNull StatutSaillie statut,   // CONFIRMEE ou ECHEC
        @NotNull LocalDate dateEcho,
        String motifEchec,              // obligatoire si statut = ECHEC
        String operateurNom,
        String notes
) {}
