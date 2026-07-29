package com.reseau_partage.stocks.dto.mouvement;

import com.reseau_partage.core.entities.enumtypes.MotifMouvement;
import com.reseau_partage.core.entities.enumtypes.TypeMouvementStock;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MouvementRequest {

    @NotNull
    private Long articleId;

    @NotNull
    private TypeMouvementStock typeMouvement;

    @NotNull
    private MotifMouvement motif;

    @NotNull
    @DecimalMin("0.001")
    private BigDecimal quantite;

    @NotNull
    private LocalDate dateMouvement;

    private BigDecimal prixUnitaire;

    private Long fournisseurId;

    private String numeroFacture;

    private String numeroLot;

    private LocalDate datePeremptionLot;

    private Long bandeId;

    private Long animalId;

    private Long vaccinationId;

    private Long entrepotDestinationId;

    private Long entrepotSourceId;

    private String entrepotDestinationNom;

    private String bandeNom;

    private String animalCode;

    private String operateurNom;

    private String notes;

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
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

    public LocalDate getDateMouvement() {
        return dateMouvement;
    }

    public void setDateMouvement(LocalDate dateMouvement) {
        this.dateMouvement = dateMouvement;
    }

    public BigDecimal getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(BigDecimal prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public Long getFournisseurId() {
        return fournisseurId;
    }

    public void setFournisseurId(Long fournisseurId) {
        this.fournisseurId = fournisseurId;
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

    public LocalDate getDatePeremptionLot() {
        return datePeremptionLot;
    }

    public void setDatePeremptionLot(LocalDate datePeremptionLot) {
        this.datePeremptionLot = datePeremptionLot;
    }

    public Long getBandeId() {
        return bandeId;
    }

    public void setBandeId(Long bandeId) {
        this.bandeId = bandeId;
    }

    public Long getAnimalId() {
        return animalId;
    }

    public void setAnimalId(Long animalId) {
        this.animalId = animalId;
    }

    public Long getVaccinationId() {
        return vaccinationId;
    }

    public void setVaccinationId(Long vaccinationId) {
        this.vaccinationId = vaccinationId;
    }

    public Long getEntrepotDestinationId() {
        return entrepotDestinationId;
    }

    public void setEntrepotDestinationId(Long entrepotDestinationId) {
        this.entrepotDestinationId = entrepotDestinationId;
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

    public Long getEntrepotSourceId() {
        return entrepotSourceId;
    }

    public void setEntrepotSourceId(Long entrepotSourceId) {
        this.entrepotSourceId = entrepotSourceId;
    }

    public String getEntrepotDestinationNom() {
        return entrepotDestinationNom;
    }

    public void setEntrepotDestinationNom(String entrepotDestinationNom) {
        this.entrepotDestinationNom = entrepotDestinationNom;
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
}