package com.reseau_partage.animaux.dto.alerte;

public record AlerteResponse(
        String type,
        String niveau,
        String message,
        Long referenceId,
        java.time.LocalDate dateCible
) {
    public AlerteResponse(String type, String niveau, String message, Long referenceId, java.time.LocalDate dateCible) {
        this.type = type;
        this.niveau = niveau;
        this.message = message;
        this.referenceId = referenceId;
        this.dateCible = dateCible;
    }
}
