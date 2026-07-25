package com.reseau_partage.animaux.dto.porcin;

import com.reseau_partage.core.entities.CauseArchivage;

public record ArchivagePorcinRequest(
        CauseArchivage cause,
        String motif
) {
}
