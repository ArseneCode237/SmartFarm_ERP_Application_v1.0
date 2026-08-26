package com.reseau_partage.stocks.entities;

import com.reseau_partage.core.entities.enumtypes.CategorieArticle;
import com.reseau_partage.core.entities.enumtypes.UniteMesure;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "alerte_stock")
public class AlerteStock {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ferme_id", nullable = false)
    private Long fermeId;

    @Column(name = "article_id", nullable = false)
    private Long articleId;

    @Column(name = "article_code", nullable = false, length = 30)
    private String articleCode;

    @Column(name = "article_designation", nullable = false, length = 200)
    private String articleDesignation;

    @Enumerated(EnumType.STRING)
    @Column(name = "article_categorie", nullable = false, length = 20)
    private CategorieArticle articleCategorie;

    @Enumerated(EnumType.STRING)
    @Column(name = "article_unite_mesure", nullable = false, length = 20)
    private UniteMesure articleUniteMesure;

    @Column(name = "article_emplacement", length = 200)
    private String articleEmplacement;

    @Column(name = "article_numero_lot", length = 100)
    private String articleNumeroLot;

    @Column(name = "article_temperature_conservation", length = 100)
    private String articleTemperatureConservation;

    @Column(name = "type_alerte", nullable = false, length = 30)
    private String typeAlerte;

    @Column(nullable = false, length = 20)
    private String priorite;

    @Column(nullable = false, length = 200)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "stock_actuel", precision = 12, scale = 3)
    private BigDecimal stockActuel;

    @Column(name = "seuil_alerte_min", precision = 12, scale = 3)
    private BigDecimal seuilAlerteMin;

    @Column(name = "seuil_alerte_critique", precision = 12, scale = 3)
    private BigDecimal seuilAlerteCritique;

    @Column(name = "date_peremption")
    private LocalDate datePeremption;

    @Column(name = "jours_avant_peremption")
    private Integer joursAvantPeremption;

    @Column(nullable = false)
    private Boolean resolue = Boolean.FALSE;

    @Column(name = "date_resolution")
    private LocalDateTime dateResolution;

    @Column(name = "commentaire_resolution", columnDefinition = "TEXT")
    private String commentaireResolution;

    @Column(name = "cree_par")
    private Long creePar;

    @Column(name = "resolu_par")
    private Long resoluPar;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification", nullable = false)
    private LocalDateTime dateModification;

    public AlerteStock() {}

    @PrePersist
    protected void onCreate() {
        this.dateCreation = LocalDateTime.now();
        this.dateModification = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.dateModification = LocalDateTime.now();
    }

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
    public CategorieArticle getArticleCategorie() { return articleCategorie; }
    public void setArticleCategorie(CategorieArticle articleCategorie) { this.articleCategorie = articleCategorie; }
    public UniteMesure getArticleUniteMesure() { return articleUniteMesure; }
    public void setArticleUniteMesure(UniteMesure articleUniteMesure) { this.articleUniteMesure = articleUniteMesure; }
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
    public Long getCreePar() { return creePar; }
    public void setCreePar(Long creePar) { this.creePar = creePar; }
    public Long getResoluPar() { return resoluPar; }
    public void setResoluPar(Long resoluPar) { this.resoluPar = resoluPar; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    public LocalDateTime getDateModification() { return dateModification; }
    public void setDateModification(LocalDateTime dateModification) { this.dateModification = dateModification; }
}
