package com.reseau_partage.animaux.dto.bovin;

import com.reseau_partage.animaux.dto.bande.BandeRequest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * Création d'une bande bovine : la bande générique (espèce BOVIN) + son extension bovine.
 */
public record BandeBovinRequest(
        @NotNull @Valid BandeRequest bande,
        @Valid ProfilBandeBovinRequest profil) {
}
