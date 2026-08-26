package com.reseau_partage.organisation.dto;

import java.util.List;

public record ImportResultResponse(
        boolean success,
        int totalLignes,
        int importes,
        int erreurs,
        List<ImportErrorDetail> details) {
}
