package com.reseau_partage.stocks.dto.article;

import com.reseau_partage.core.entities.Espece;
import com.reseau_partage.core.entities.enumtypes.CategorieArticle;
import com.reseau_partage.core.entities.enumtypes.StatutStock;
import com.reseau_partage.core.entities.enumtypes.UniteMesure;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ArticleResponse {

    private Long id;
    private String codeArticle;
    private String designation;
    private String description;
    private CategorieArticle categorie;
    private UniteMesure uniteMesure;
    private Long fermeId;
    private Long entrepotId;
    private String entrepotNom;
    private BigDecimal stockActuel;
    private BigDecimal stockInitial;
    private BigDecimal seuilAlerteMin;
    private BigDecimal seuilAlerteCritique;
    private BigDecimal stockMax;
    private StatutStock statut;
    private BigDecimal prixUnitaireRef;
    private BigDecimal valeurStock;
    private Double tauxRemplissagePct;
    private Integer joursStockRestants;
    private LocalDate datePeremption;
    private Integer joursAvantPeremption;
    private List<Espece> especesLiees;
    private String fournisseurNom;
    private LocalDate dateDerniereEntree;
    private LocalDate dateDerniereSortie;
    private LocalDateTime dateCreation;
    private LocalDateTime dateDerniereMiseAJour;
    private Long nombreMouvements;
    private LocalDate dateDernierMouvement;
    private Map<String, Object> fournisseurPrincipal;
    private String emplacementStockage;
    private String numeroLot;
    private String notesInternes;
    private Boolean estPerissable;
    private Boolean estSuiviLot;
    private String temperatureConservation;
    private LocalDate dateEntree;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodeArticle() {
        return codeArticle;
    }

    public void setCodeArticle(String codeArticle) {
        this.codeArticle = codeArticle;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

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

    public Long getFermeId() {
        return fermeId;
    }

    public void setFermeId(Long fermeId) {
        this.fermeId = fermeId;
    }

    public Long getEntrepotId() {
        return entrepotId;
    }

    public void setEntrepotId(Long entrepotId) {
        this.entrepotId = entrepotId;
    }

    public String getEntrepotNom() {
        return entrepotNom;
    }

    public void setEntrepotNom(String entrepotNom) {
        this.entrepotNom = entrepotNom;
    }

    public BigDecimal getStockActuel() {
        return stockActuel;
    }

    public void setStockActuel(BigDecimal stockActuel) {
        this.stockActuel = stockActuel;
    }

    public BigDecimal getStockInitial() {
        return stockInitial;
    }

    public void setStockInitial(BigDecimal stockInitial) {
        this.stockInitial = stockInitial;
    }

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

    public StatutStock getStatut() {
        return statut;
    }

    public void setStatut(StatutStock statut) {
        this.statut = statut;
    }

    public BigDecimal getPrixUnitaireRef() {
        return prixUnitaireRef;
    }

    public void setPrixUnitaireRef(BigDecimal prixUnitaireRef) {
        this.prixUnitaireRef = prixUnitaireRef;
    }

    public BigDecimal getValeurStock() {
        return valeurStock;
    }

    public void setValeurStock(BigDecimal valeurStock) {
        this.valeurStock = valeurStock;
    }

    public Double getTauxRemplissagePct() {
        return tauxRemplissagePct;
    }

    public void setTauxRemplissagePct(Double tauxRemplissagePct) {
        this.tauxRemplissagePct = tauxRemplissagePct;
    }

    public Integer getJoursStockRestants() {
        return joursStockRestants;
    }

    public void setJoursStockRestants(Integer joursStockRestants) {
        this.joursStockRestants = joursStockRestants;
    }

    public LocalDate getDatePeremption() {
        return datePeremption;
    }

    public void setDatePeremption(LocalDate datePeremption) {
        this.datePeremption = datePeremption;
    }

    public Integer getJoursAvantPeremption() {
        return joursAvantPeremption;
    }

    public void setJoursAvantPeremption(Integer joursAvantPeremption) {
        this.joursAvantPeremption = joursAvantPeremption;
    }

    public List<Espece> getEspecesLiees() {
        return especesLiees;
    }

    public void setEspecesLiees(List<Espece> especesLiees) {
        this.especesLiees = especesLiees;
    }

    public String getFournisseurNom() {
        return fournisseurNom;
    }

    public void setFournisseurNom(String fournisseurNom) {
        this.fournisseurNom = fournisseurNom;
    }

    public LocalDate getDateDerniereEntree() {
        return dateDerniereEntree;
    }

    public void setDateDerniereEntree(LocalDate dateDerniereEntree) {
        this.dateDerniereEntree = dateDerniereEntree;
    }

    public LocalDate getDateDerniereSortie() {
        return dateDerniereSortie;
    }

    public void setDateDerniereSortie(LocalDate dateDerniereSortie) {
        this.dateDerniereSortie = dateDerniereSortie;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    // Champs du contrat front-end. Les accesseurs historiques restent pour les autres consommateurs.
    public String getNom() { return designation; }
    public String getReference() { return codeArticle; }
    public BigDecimal getSeuilAlerte() { return seuilAlerteMin; }
    public BigDecimal getSeuilCritique() { return seuilAlerteCritique; }
    public BigDecimal getCoutUnitaire() { return prixUnitaireRef; }
    public BigDecimal getValeurStockActuel() { return valeurStock; }
    public String getEntrepot() { return entrepotNom; }
    public LocalDateTime getDateDerniereMiseAJour() { return dateDerniereMiseAJour; }
    public void setDateDerniereMiseAJour(LocalDateTime value) { this.dateDerniereMiseAJour = value; }
    public Long getNombreMouvements() { return nombreMouvements; }
    public void setNombreMouvements(Long value) { this.nombreMouvements = value; }
    public LocalDate getDateDernierMouvement() { return dateDernierMouvement; }
    public void setDateDernierMouvement(LocalDate value) { this.dateDernierMouvement = value; }
    public Map<String, Object> getFournisseurPrincipal() { return fournisseurPrincipal; }
    public void setFournisseurPrincipal(Map<String, Object> value) { this.fournisseurPrincipal = value; }
    public String getEmplacementStockage() { return emplacementStockage; }
    public void setEmplacementStockage(String value) { this.emplacementStockage = value; }
    public String getNumeroLot() { return numeroLot; }
    public void setNumeroLot(String value) { this.numeroLot = value; }
    public String getNotesInternes() { return notesInternes; }
    public void setNotesInternes(String value) { this.notesInternes = value; }
    public Boolean getEstPerissable() { return estPerissable; }
    public void setEstPerissable(Boolean value) { this.estPerissable = value; }
    public Boolean getEstSuiviLot() { return estSuiviLot; }
    public void setEstSuiviLot(Boolean value) { this.estSuiviLot = value; }
    public String getTemperatureConservation() { return temperatureConservation; }
    public void setTemperatureConservation(String value) { this.temperatureConservation = value; }
    public LocalDate getDateEntree() { return dateEntree; }
    public void setDateEntree(LocalDate value) { this.dateEntree = value; }
}
