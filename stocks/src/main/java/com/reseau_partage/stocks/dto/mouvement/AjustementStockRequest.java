package com.reseau_partage.stocks.dto.mouvement;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AjustementStockRequest {

    @NotNull
    private Long fermeId;

    @NotNull
    private Long articleId;

    @NotNull
    @DecimalMin("0")
    private BigDecimal stockReelConstate;

    private LocalDate dateInventaire;

    private String operateurNom;

    private String notes;

    public Long getFermeId() { return fermeId; }
    public void setFermeId(Long fermeId) { this.fermeId = fermeId; }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public BigDecimal getStockReelConstate() {
        return stockReelConstate;
    }

    public void setStockReelConstate(BigDecimal stockReelConstate) {
        this.stockReelConstate = stockReelConstate;
    }

    public LocalDate getDateInventaire() {
        return dateInventaire;
    }

    public void setDateInventaire(LocalDate dateInventaire) {
        this.dateInventaire = dateInventaire;
    }

    public String getOperateurNom() {
        return operateurNom;
    }

    public void setOperateurNom(String operateurNom) {
        this.operateurNom = operateurNom;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
