package com.reseau_partage.stocks.dto.mouvement;

import com.reseau_partage.core.entities.enumtypes.MotifMouvement;
import com.reseau_partage.core.entities.enumtypes.TypeMouvementStock;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MouvementRequest {

    @NotNull
    private Long fermeId;

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

    private String uniteMesure;

    private String destinationOrigine;

    private String fournisseurNom;

    private String numeroBon;

    private BigDecimal coutUnitaire;

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

    private String operateur;

    private String notes;

    public Long getFermeId() { return fermeId; }

    public void setFermeId(Long fermeId) { this.fermeId = fermeId; }

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

    public String getUniteMesure() { return uniteMesure; }
    public void setUniteMesure(String uniteMesure) { this.uniteMesure = uniteMesure; }

    public String getDestinationOrigine() { return destinationOrigine; }
    public void setDestinationOrigine(String destinationOrigine) { this.destinationOrigine = destinationOrigine; }

    public String getFournisseurNom() { return fournisseurNom; }
    public void setFournisseurNom(String fournisseurNom) { this.fournisseurNom = fournisseurNom; }

    public String getNumeroBon() { return numeroBon; }
    public void setNumeroBon(String numeroBon) { this.numeroBon = numeroBon; }

    public BigDecimal getCoutUnitaire() {
        return coutUnitaire;
    }

    public void setCoutUnitaire(BigDecimal coutUnitaire) {
        this.coutUnitaire = coutUnitaire;
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

    public String getOperateur() {
        return operateur;
    }

    public void setOperateur(String operateur) {
        this.operateur = operateur;
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
