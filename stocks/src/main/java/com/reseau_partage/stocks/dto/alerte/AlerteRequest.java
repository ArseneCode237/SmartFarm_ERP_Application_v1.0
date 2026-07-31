package com.reseau_partage.stocks.dto.alerte;

import jakarta.validation.constraints.NotNull;

public class AlerteRequest {

    private String commentaire;

    @NotNull(message = "fermeId est obligatoire")
    private Long fermeId;

    public AlerteRequest() {}

    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }
    public Long getFermeId() { return fermeId; }
    public void setFermeId(Long fermeId) { this.fermeId = fermeId; }
}
