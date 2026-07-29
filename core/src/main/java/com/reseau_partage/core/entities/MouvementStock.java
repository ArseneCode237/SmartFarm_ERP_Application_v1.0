package com.reseau_partage.core.entities;

import com.reseau_partage.core.entities.enumtypes.MotifMouvement;
import com.reseau_partage.core.entities.enumtypes.TypeMouvementStock;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "mouvements_stock",
       indexes = {
           @Index(name = "idx_mvt_article", columnList = "article_id"),
           @Index(name = "idx_mvt_ferme", columnList = "ferme_id"),
           @Index(name = "idx_mvt_date", columnList = "date_mouvement"),
           @Index(name = "idx_mvt_type", columnList = "type_mouvement")
       })
public class MouvementStock {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @Column(name = "ferme_id", nullable = false)
    private Long fermeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_mouvement", nullable = false, length = 20)
    private TypeMouvementStock typeMouvement;

    @Enumerated(EnumType.STRING)
    @Column(name = "motif_mouvement", length = 30)
    private MotifMouvement motif;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal quantite;

    @Column(name = "stock_avant", precision = 12, scale = 3)
    private BigDecimal stockAvant;

    @Column(name = "stock_apres", precision = 12, scale = 3)
    private BigDecimal stockApres;

    @Column(name = "prix_unitaire", precision = 15, scale = 2)
    private BigDecimal prixUnitaire;

    @Column(name = "montant_total", precision = 15, scale = 2)
    private BigDecimal montantTotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fournisseur_id")
    private Fournisseur fournisseur;

    @Column(name = "numero_facture", length = 100)
    private String numeroFacture;

    @Column(name = "numero_lot", length = 100)
    private String numeroLot;

    @Column(name = "date_peremption_lot")
    private LocalDate datePeremptionLot;

    @Column(name = "bande_id")
    private Long bandeId;

    @Column(name = "bande_nom", length = 100)
    private String bandeNom;

    @Column(name = "animal_id")
    private Long animalId;

    @Column(name = "animal_code", length = 30)
    private String animalCode;

    @Column(name = "vaccination_id")
    private Long vaccinationId;

    @Column(name = "entrepot_source_id")
    private Long entrepotSourceId;

    @Column(name = "entrepot_destination_id")
    private Long entrepotDestinationId;

    @Column(name = "entrepot_destination_nom", length = 150)
    private String entrepotDestinationNom;

    @Column(name = "date_mouvement", nullable = false)
    private LocalDate dateMouvement;

    @Column(name = "operateur_nom", length = 100)
    private String operateurNom;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    public MouvementStock() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public Long getFermeId() {
        return fermeId;
    }

    public void setFermeId(Long fermeId) {
        this.fermeId = fermeId;
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

    public Fournisseur getFournisseur() {
        return fournisseur;
    }

    public void setFournisseur(Fournisseur fournisseur) {
        this.fournisseur = fournisseur;
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

    public String getBandeNom() {
        return bandeNom;
    }

    public void setBandeNom(String bandeNom) {
        this.bandeNom = bandeNom;
    }

    public Long getAnimalId() {
        return animalId;
    }

    public void setAnimalId(Long animalId) {
        this.animalId = animalId;
    }

    public String getAnimalCode() {
        return animalCode;
    }

    public void setAnimalCode(String animalCode) {
        this.animalCode = animalCode;
    }

    public Long getVaccinationId() {
        return vaccinationId;
    }

    public void setVaccinationId(Long vaccinationId) {
        this.vaccinationId = vaccinationId;
    }

    public Long getEntrepotSourceId() {
        return entrepotSourceId;
    }

    public void setEntrepotSourceId(Long entrepotSourceId) {
        this.entrepotSourceId = entrepotSourceId;
    }

    public Long getEntrepotDestinationId() {
        return entrepotDestinationId;
    }

    public void setEntrepotDestinationId(Long entrepotDestinationId) {
        this.entrepotDestinationId = entrepotDestinationId;
    }

    public String getEntrepotDestinationNom() {
        return entrepotDestinationNom;
    }

    public void setEntrepotDestinationNom(String entrepotDestinationNom) {
        this.entrepotDestinationNom = entrepotDestinationNom;
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

    @PrePersist
    protected void onCreate() {
        this.dateCreation = LocalDateTime.now();
        if (this.prixUnitaire != null && this.quantite != null) {
            this.montantTotal = this.prixUnitaire.multiply(this.quantite);
        }
    }
}