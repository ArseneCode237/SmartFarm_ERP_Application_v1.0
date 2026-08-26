package com.reseau_partage.core.entities;

import com.reseau_partage.core.entities.Espece;
import com.reseau_partage.core.entities.enumtypes.CategorieArticle;
import com.reseau_partage.core.entities.enumtypes.StatutStock;
import com.reseau_partage.core.entities.enumtypes.UniteMesure;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "articles",
       indexes = {
           @Index(name = "idx_art_ferme", columnList = "ferme_id"),
           @Index(name = "idx_art_categorie", columnList = "categorie"),
           @Index(name = "idx_art_statut", columnList = "statut")
       })
public class Article {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code_article", nullable = false, unique = true, length = 30)
    private String codeArticle;

    @Column(nullable = false, length = 200)
    private String designation;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CategorieArticle categorie;

    @Enumerated(EnumType.STRING)
    @Column(name = "unite_mesure", nullable = false, length = 20)
    private UniteMesure uniteMesure;

    @Column(name = "ferme_id", nullable = false)
    private Long fermeId;

    @Column(name = "entrepot_id")
    private Long entrepotId;

    @Column(name = "entrepot_nom", length = 150)
    private String entrepotNom;

    @Column(name = "site_id")
    private Long siteId;

    @Column(name = "site_nom", length = 150)
    private String siteNom;

    @Column(name = "stock_actuel", nullable = false, precision = 12, scale = 3)
    private BigDecimal stockActuel = BigDecimal.ZERO;

    @Column(name = "stock_initial", precision = 12, scale = 3)
    private BigDecimal stockInitial = BigDecimal.ZERO;

    @Column(name = "seuil_alerte_min", nullable = false, precision = 12, scale = 3)
    private BigDecimal seuilAlerteMin;

    @Column(name = "seuil_alerte_critique", precision = 12, scale = 3)
    private BigDecimal seuilAlerteCritique;

    @Column(name = "stock_max", precision = 12, scale = 3)
    private BigDecimal stockMax;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutStock statut = StatutStock.NORMAL;

    @Column(name = "prix_unitaire_ref", precision = 15, scale = 2)
    private BigDecimal prixUnitaireRef;

    @Column(name = "valeur_stock", precision = 15, scale = 2)
    private BigDecimal valeurStock;

    @Column(name = "date_peremption")
    private LocalDate datePeremption;

    @Column(name = "alerte_peremption_jours")
    private Integer alertePeremptionJours = 30;

    @ElementCollection
    @CollectionTable(
        name = "article_especes",
        joinColumns = @JoinColumn(name = "article_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "espece")
    private List<Espece> especesLiees = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fournisseur_id")
    private Fournisseur fournisseurHabituel;

    @Column(name = "date_derniere_entree")
    private LocalDate dateDerniereEntree;

    @Column(name = "date_derniere_sortie")
    private LocalDate dateDerniereSortie;

    @Column(name = "date_dernier_inventaire")
    private LocalDate dateDernierInventaire;

    @Column(name = "emplacement_stockage", length = 200)
    private String emplacementStockage;

    @Column(name = "numero_lot", length = 100)
    private String numeroLot;

    @Column(name = "notes_internes", columnDefinition = "TEXT")
    private String notesInternes;

    @Column(name = "est_perissable", nullable = false)
    private Boolean estPerissable = false;

    @Column(name = "est_suivi_lot", nullable = false)
    private Boolean estSuiviLot = false;

    @Column(name = "temperature_conservation", length = 100)
    private String temperatureConservation;

    @Column(name = "date_entree")
    private LocalDate dateEntree;

    @Column(name = "actif", nullable = false)
    private Boolean actif = true;

    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    public Article() {
    }

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

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getSiteNom() {
        return siteNom;
    }

    public void setSiteNom(String siteNom) {
        this.siteNom = siteNom;
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

    public Fournisseur getFournisseurHabituel() {
        return fournisseurHabituel;
    }

    public void setFournisseurHabituel(Fournisseur fournisseurHabituel) {
        this.fournisseurHabituel = fournisseurHabituel;
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

    public LocalDate getDateDernierInventaire() {
        return dateDernierInventaire;
    }

    public void setDateDernierInventaire(LocalDate dateDernierInventaire) {
        this.dateDernierInventaire = dateDernierInventaire;
    }

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

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDateTime getDateModification() {
        return dateModification;
    }

    public void setDateModification(LocalDateTime dateModification) {
        this.dateModification = dateModification;
    }

    @PrePersist
    protected void onCreate() {
        this.dateCreation = LocalDateTime.now();
        this.dateModification = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.dateModification = LocalDateTime.now();
    }
}
