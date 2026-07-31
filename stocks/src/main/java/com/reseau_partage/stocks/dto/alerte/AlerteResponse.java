package com.reseau_partage.stocks.dto.alerte;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class AlerteResponse {

    private Long id;
    private Long fermeId;
    private Long articleId;
    private String articleCode;
    private String articleDesignation;
    private String articleCategorie;
    private String articleUniteMesure;
    private String articleEmplacement;
    private String articleNumeroLot;
    private String articleTemperatureConservation;
    private String typeAlerte;
    private String priorite;
    private String titre;
    private String description;
    private BigDecimal stockActuel;
    private BigDecimal seuilAlerteMin;
    private BigDecimal seuilAlerteCritique;
    private LocalDate datePeremption;
    private Integer joursAvantPeremption;
    private Boolean resolue;
    private LocalDateTime dateResolution;
    private String commentaireResolution;
    private LocalDateTime dateCreation;

    public AlerteResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getFermeId() { return fermeId; }
    public void setFermeId(Long fermeId) { this.fermeId = fermeId; }
    public Long getArticleId() { return articleId; }
    public void setArticleId(Long articleId) { this.articleId = articleId; }
    public String getArticleCode() { return articleCode; }
    public void setArticleCode(String articleCode) { this.articleCode = articleCode; }
    public String getArticleDesignation() { return articleDesignation; }
    public void setArticleDesignation(String articleDesignation) { this.articleDesignation = articleDesignation; }
    public String getArticleCategorie() { return articleCategorie; }
    public void setArticleCategorie(String articleCategorie) { this.articleCategorie = articleCategorie; }
    public String getArticleUniteMesure() { return articleUniteMesure; }
    public void setArticleUniteMesure(String articleUniteMesure) { this.articleUniteMesure = articleUniteMesure; }
    public String getArticleEmplacement() { return articleEmplacement; }
    public void setArticleEmplacement(String articleEmplacement) { this.articleEmplacement = articleEmplacement; }
    public String getArticleNumeroLot() { return articleNumeroLot; }
    public void setArticleNumeroLot(String articleNumeroLot) { this.articleNumeroLot = articleNumeroLot; }
    public String getArticleTemperatureConservation() { return articleTemperatureConservation; }
    public void setArticleTemperatureConservation(String articleTemperatureConservation) { this.articleTemperatureConservation = articleTemperatureConservation; }
    public String getTypeAlerte() { return typeAlerte; }
    public void setTypeAlerte(String typeAlerte) { this.typeAlerte = typeAlerte; }
    public String getPriorite() { return priorite; }
    public void setPriorite(String priorite) { this.priorite = priorite; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getStockActuel() { return stockActuel; }
    public void setStockActuel(BigDecimal stockActuel) { this.stockActuel = stockActuel; }
    public BigDecimal getSeuilAlerteMin() { return seuilAlerteMin; }
    public void setSeuilAlerteMin(BigDecimal seuilAlerteMin) { this.seuilAlerteMin = seuilAlerteMin; }
    public BigDecimal getSeuilAlerteCritique() { return seuilAlerteCritique; }
    public void setSeuilAlerteCritique(BigDecimal seuilAlerteCritique) { this.seuilAlerteCritique = seuilAlerteCritique; }
    public LocalDate getDatePeremption() { return datePeremption; }
    public void setDatePeremption(LocalDate datePeremption) { this.datePeremption = datePeremption; }
    public Integer getJoursAvantPeremption() { return joursAvantPeremption; }
    public void setJoursAvantPeremption(Integer joursAvantPeremption) { this.joursAvantPeremption = joursAvantPeremption; }
    public Boolean getResolue() { return resolue; }
    public void setResolue(Boolean resolue) { this.resolue = resolue; }
    public LocalDateTime getDateResolution() { return dateResolution; }
    public void setDateResolution(LocalDateTime dateResolution) { this.dateResolution = dateResolution; }
    public String getCommentaireResolution() { return commentaireResolution; }
    public void setCommentaireResolution(String commentaireResolution) { this.commentaireResolution = commentaireResolution; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
}
