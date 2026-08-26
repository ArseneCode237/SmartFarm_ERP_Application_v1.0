package com.reseau_partage.stocks.dto.mouvement;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.reseau_partage.core.entities.enumtypes.MotifMouvement;
import com.reseau_partage.core.entities.enumtypes.TypeMouvementStock;

public class MouvementResponse {
    private Long id;
    private TypeMouvementStock typeMouvement;
    private Long articleId;
    private ArticleResume article;
    private BigDecimal quantite;
    private String uniteMesure;
    private LocalDate dateMouvement;
    private MotifMouvement motif;
    private String destinationOrigine;
    private Long bandeId;
    private String fournisseurNom;
    private String numeroBon;
    private BigDecimal coutUnitaire;
    private BigDecimal coutTotal;
    private String numeroLot;
    private String operateur;
    private String notes;
    private Long fermeId;
    private LocalDateTime dateCreation;
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public TypeMouvementStock getTypeMouvement() { return typeMouvement; } public void setTypeMouvement(TypeMouvementStock v) { typeMouvement = v; }
    public Long getArticleId() { return articleId; } public void setArticleId(Long v) { articleId = v; }
    public ArticleResume getArticle() { return article; } public void setArticle(ArticleResume v) { article = v; }
    public BigDecimal getQuantite() { return quantite; } public void setQuantite(BigDecimal v) { quantite = v; }
    public String getUniteMesure() { return uniteMesure; } public void setUniteMesure(String v) { uniteMesure = v; }
    public LocalDate getDateMouvement() { return dateMouvement; } public void setDateMouvement(LocalDate v) { dateMouvement = v; }
    public MotifMouvement getMotif() { return motif; } public void setMotif(MotifMouvement v) { motif = v; }
    public String getDestinationOrigine() { return destinationOrigine; } public void setDestinationOrigine(String v) { destinationOrigine = v; }
    public Long getBandeId() { return bandeId; } public void setBandeId(Long v) { bandeId = v; }
    public String getFournisseurNom() { return fournisseurNom; } public void setFournisseurNom(String v) { fournisseurNom = v; }
    public String getNumeroBon() { return numeroBon; } public void setNumeroBon(String v) { numeroBon = v; }
    public BigDecimal getCoutUnitaire() { return coutUnitaire; } public void setCoutUnitaire(BigDecimal v) { coutUnitaire = v; }
    public BigDecimal getCoutTotal() { return coutTotal; } public void setCoutTotal(BigDecimal v) { coutTotal = v; }
    public String getNumeroLot() { return numeroLot; } public void setNumeroLot(String v) { numeroLot = v; }
    public String getOperateur() { return operateur; } public void setOperateur(String v) { operateur = v; }
    public String getNotes() { return notes; } public void setNotes(String v) { notes = v; }
    public Long getFermeId() { return fermeId; } public void setFermeId(Long v) { fermeId = v; }
    public LocalDateTime getDateCreation() { return dateCreation; } public void setDateCreation(LocalDateTime v) { dateCreation = v; }
    public static class ArticleResume {
        private Long id; private String nom; private String reference;
        public ArticleResume() { }
        public ArticleResume(Long id, String nom, String reference) { this.id = id; this.nom = nom; this.reference = reference; }
        public Long getId() { return id; } public void setId(Long v) { id = v; }
        public String getNom() { return nom; } public void setNom(String v) { nom = v; }
        public String getReference() { return reference; } public void setReference(String v) { reference = v; }
    }
}
