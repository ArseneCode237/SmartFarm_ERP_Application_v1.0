package com.reseau_partage.stocks.dto.boncommande;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class LigneBonCommandeRequest {

    @NotNull
    private Long articleId;

    @NotNull
    @DecimalMin("0.001")
    private BigDecimal quantiteCommandee;

    private BigDecimal prixUnitaire;

    private BigDecimal quantiteRecue;

    private String numeroLot;

    private LocalDate datePeremptionLot;

    private BigDecimal prixUnitaireReel;

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public BigDecimal getQuantiteCommandee() {
        return quantiteCommandee;
    }

    public void setQuantiteCommandee(BigDecimal quantiteCommandee) {
        this.quantiteCommandee = quantiteCommandee;
    }

    public BigDecimal getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(BigDecimal prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public BigDecimal getQuantiteRecue() {
        return quantiteRecue;
    }

    public void setQuantiteRecue(BigDecimal quantiteRecue) {
        this.quantiteRecue = quantiteRecue;
    }

    public String getNumeroLot() {
        return numeroLot;
    }

    public void setNumeroLot(String numeroLot) {
        this.numeroLot = numeroLot;
    }

    public LocalDate getDatePeremptionLot() {
        return datePeremptionLot;
    }

    public void setDatePeremptionLot(LocalDate datePeremptionLot) {
        this.datePeremptionLot = datePeremptionLot;
    }

    public BigDecimal getPrixUnitaireReel() {
        return prixUnitaireReel;
    }

    public void setPrixUnitaireReel(BigDecimal prixUnitaireReel) {
        this.prixUnitaireReel = prixUnitaireReel;
    }
}