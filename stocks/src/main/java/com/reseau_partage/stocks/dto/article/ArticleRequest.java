package com.reseau_partage.stocks.dto.article;

import com.reseau_partage.core.entities.Espece;
import com.reseau_partage.core.entities.enumtypes.CategorieArticle;
import com.reseau_partage.core.entities.enumtypes.UniteMesure;
import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ArticleRequest {

    @NotBlank
    @JsonAlias("nom")
    private String designation;

    @JsonAlias("reference")
    private String codeArticle;

    private String description;

    @NotNull
    private CategorieArticle categorie;

    private UniteMesure uniteMesure;

    private Long entrepotId;

    private String entrepot;

    @DecimalMin("0")
    @JsonAlias("seuilAlerte")
    private BigDecimal seuilAlerteMin;

    @JsonAlias("seuilCritique")
    private BigDecimal seuilAlerteCritique;

    private BigDecimal stockMax;

    private BigDecimal stockInitial;

    @JsonAlias("coutUnitaire")
    private BigDecimal prixUnitaireRef;

    private LocalDate datePeremption;

    private Integer alertePeremptionJours;

    private List<Espece> especesLiees;

    @JsonAlias("fournisseurPrincipalId")
    private Long fournisseurId;

    private String fournisseurPrincipalNom;

    private Long fermeId;

    private BigDecimal stockActuel;
    private String emplacementStockage;
    private String numeroLot;
    private String notesInternes;
    private Boolean estPerissable;
    private Boolean estSuiviLot;
    private String temperatureConservation;
    private LocalDate dateEntree;

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getCodeArticle() { return codeArticle; }
    public void setCodeArticle(String codeArticle) { this.codeArticle = codeArticle; }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CategorieArticle getCategorie() {
        return categorie;
    }

    public void setCategorie(CategorieArticle categorie) {
        this.categorie = categorie;
    }

    public UniteMesure getUniteMesure() {
        return uniteMesure;
    }

    public void setUniteMesure(UniteMesure uniteMesure) {
        this.uniteMesure = uniteMesure;
    }

    public Long getEntrepotId() {
        return entrepotId;
    }

    public void setEntrepotId(Long entrepotId) {
        this.entrepotId = entrepotId;
    }

    public String getEntrepot() { return entrepot; }
    public void setEntrepot(String entrepot) { this.entrepot = entrepot; }

    public BigDecimal getSeuilAlerteMin() {
        return seuilAlerteMin;
    }

    public void setSeuilAlerteMin(BigDecimal seuilAlerteMin) {
        this.seuilAlerteMin = seuilAlerteMin;
    }

    public BigDecimal getSeuilAlerteCritique() {
        return seuilAlerteCritique;
    }

    public void setSeuilAlerteCritique(BigDecimal seuilAlerteCritique) {
        this.seuilAlerteCritique = seuilAlerteCritique;
    }

    public BigDecimal getStockMax() {
        return stockMax;
    }

    public void setStockMax(BigDecimal stockMax) {
        this.stockMax = stockMax;
    }

    public BigDecimal getStockInitial() {
        return stockInitial;
    }

    public void setStockInitial(BigDecimal stockInitial) {
        this.stockInitial = stockInitial;
    }

    public BigDecimal getPrixUnitaireRef() {
        return prixUnitaireRef;
    }

    public void setPrixUnitaireRef(BigDecimal prixUnitaireRef) {
        this.prixUnitaireRef = prixUnitaireRef;
    }

    public LocalDate getDatePeremption() {
        return datePeremption;
    }

    public void setDatePeremption(LocalDate datePeremption) {
        this.datePeremption = datePeremption;
    }

    public Integer getAlertePeremptionJours() {
        return alertePeremptionJours;
    }

    public void setAlertePeremptionJours(Integer alertePeremptionJours) {
        this.alertePeremptionJours = alertePeremptionJours;
    }

    public List<Espece> getEspecesLiees() {
        return especesLiees;
    }

    public void setEspecesLiees(List<Espece> especesLiees) {
        this.especesLiees = especesLiees;
    }

    public Long getFournisseurId() {
        return fournisseurId;
    }

    public void setFournisseurId(Long fournisseurId) {
        this.fournisseurId = fournisseurId;
    }

    public String getFournisseurPrincipalNom() { return fournisseurPrincipalNom; }
    public void setFournisseurPrincipalNom(String fournisseurPrincipalNom) { this.fournisseurPrincipalNom = fournisseurPrincipalNom; }

    public Long getFermeId() {
        return fermeId;
    }

    public void setFermeId(Long fermeId) {
        this.fermeId = fermeId;
    }

    public BigDecimal getStockActuel() { return stockActuel; }
    public void setStockActuel(BigDecimal stockActuel) { this.stockActuel = stockActuel; }
    public String getEmplacementStockage() { return emplacementStockage; }
    public void setEmplacementStockage(String emplacementStockage) { this.emplacementStockage = emplacementStockage; }
    public String getNumeroLot() { return numeroLot; }
    public void setNumeroLot(String numeroLot) { this.numeroLot = numeroLot; }
    public String getNotesInternes() { return notesInternes; }
    public void setNotesInternes(String notesInternes) { this.notesInternes = notesInternes; }
    public Boolean getEstPerissable() { return estPerissable; }
    public void setEstPerissable(Boolean estPerissable) { this.estPerissable = estPerissable; }
    public Boolean getEstSuiviLot() { return estSuiviLot; }
    public void setEstSuiviLot(Boolean estSuiviLot) { this.estSuiviLot = estSuiviLot; }
    public String getTemperatureConservation() { return temperatureConservation; }
    public void setTemperatureConservation(String temperatureConservation) { this.temperatureConservation = temperatureConservation; }
    public LocalDate getDateEntree() { return dateEntree; }
    public void setDateEntree(LocalDate dateEntree) { this.dateEntree = dateEntree; }
}
