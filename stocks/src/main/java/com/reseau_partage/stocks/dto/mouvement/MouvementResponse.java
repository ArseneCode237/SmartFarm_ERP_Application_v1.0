package com.reseau_partage.stocks.dto.mouvement;

import com.reseau_partage.core.entities.enumtypes.MotifMouvement;
import com.reseau_partage.core.entities.enumtypes.TypeMouvementStock;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class MouvementResponse {

    private Long id;
    private Long articleId;
    private String articleDesignation;
    private String articleCode;
    private TypeMouvementStock typeMouvement;
    private MotifMouvement motif;
    private BigDecimal quantite;
    private BigDecimal stockAvant;
    private BigDecimal stockApres;
    private BigDecimal prixUnitaire;
    private BigDecimal montantTotal;
    private String fournisseurNom;
    private String numeroFacture;
    private String numeroLot;
    private String bandeNom;
    private String animalCode;
    private LocalDate dateMouvement;
    private String operateurNom;
    private String notes;
    private LocalDateTime dateCreation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public String getArticleDesignation() {
        return articleDesignation;
    }

    public void setArticleDesignation(String articleDesignation) {
        this.articleDesignation = articleDesignation;
    }

    public String getArticleCode() {
        return articleCode;
    }

    public void setArticleCode(String articleCode) {
        this.articleCode = articleCode;
    }

    public TypeMouvementStock getTypeMouvement() {
        return typeMouvement;
    }

    public void setTypeMouvement(TypeMouvementStock typeMouvement) {
        this.typeMouvement = typeMouvement;
    }

    public MotifMouvement getMotif() {
        return motif;
    }

    public void setMotif(MotifMouvement motif) {
        this.motif = motif;
    }

    public BigDecimal getQuantite() {
        return quantite;
    }

    public void setQuantite(BigDecimal quantite) {
        this.quantite = quantite;
    }

    public BigDecimal getStockAvant() {
        return stockAvant;
    }

    public void setStockAvant(BigDecimal stockAvant) {
        this.stockAvant = stockAvant;
    }

    public BigDecimal getStockApres() {
        return stockApres;
    }

    public void setStockApres(BigDecimal stockApres) {
        this.stockApres = stockApres;
    }

    public BigDecimal getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(BigDecimal prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public BigDecimal getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(BigDecimal montantTotal) {
        this.montantTotal = montantTotal;
    }

    public String getFournisseurNom() {
        return fournisseurNom;
    }

    public void setFournisseurNom(String fournisseurNom) {
        this.fournisseurNom = fournisseurNom;
    }

    public String getNumeroFacture() {
        return numeroFacture;
    }

    public void setNumeroFacture(String numeroFacture) {
        this.numeroFacture = numeroFacture;
    }

    public String getNumeroLot() {
        return numeroLot;
    }

    public void setNumeroLot(String numeroLot) {
        this.numeroLot = numeroLot;
    }

    public String getBandeNom() {
        return bandeNom;
    }

    public void setBandeNom(String bandeNom) {
        this.bandeNom = bandeNom;
    }

    public String getAnimalCode() {
        return animalCode;
    }

    public void setAnimalCode(String animalCode) {
        this.animalCode = animalCode;
    }

    public LocalDate getDateMouvement() {
        return dateMouvement;
    }

    public void setDateMouvement(LocalDate dateMouvement) {
        this.dateMouvement = dateMouvement;
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

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
}