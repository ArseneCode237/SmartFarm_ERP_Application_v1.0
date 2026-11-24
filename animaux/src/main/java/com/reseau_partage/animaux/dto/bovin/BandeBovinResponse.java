package com.reseau_partage.animaux.dto.bovin;

import com.reseau_partage.animaux.dto.bande.BandeResponse;

/** Bande bovine = bande générique + son profil bovin. */
public record BandeBovinResponse(
        BandeResponse bande,
        ProfilBandeBovinResponse profil) {
}
