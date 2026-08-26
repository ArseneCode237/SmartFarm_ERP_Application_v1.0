package com.reseau_partage.stocks.dto.boncommande;

import java.math.BigDecimal;

/** Ligne d'un bon de commande (version sérialisable, sans entités JPA). */
public class LigneBonCommandeResponse {

    private Long id;
    private Long articleId;
    private String articleNom;
    private String articleReference;
    private String uniteMesure;
    private BigDecimal quantiteCommandee;
    private BigDecimal quantiteRecue;
    private BigDecimal prixUnitaire;
    private BigDecimal montantLigne;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getArticleId() { return articleId; }
    public void setArticleId(Long articleId) { this.articleId = articleId; }

    public String getArticleNom() { return articleNom; }
    public void setArticleNom(String articleNom) { this.articleNom = articleNom; }

    public String getArticleReference() { return articleReference; }
    public void setArticleReference(String articleReference) { this.articleReference = articleReference; }

    public String getUniteMesure() { return uniteMesure; }
    public void setUniteMesure(String uniteMesure) { this.uniteMesure = uniteMesure; }

    public BigDecimal getQuantiteCommandee() { return quantiteCommandee; }
    public void setQuantiteCommandee(BigDecimal quantiteCommandee) { this.quantiteCommandee = quantiteCommandee; }

    public BigDecimal getQuantiteRecue() { return quantiteRecue; }
    public void setQuantiteRecue(BigDecimal quantiteRecue) { this.quantiteRecue = quantiteRecue; }

    public BigDecimal getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(BigDecimal prixUnitaire) { this.prixUnitaire = prixUnitaire; }

    public BigDecimal getMontantLigne() { return montantLigne; }
    public void setMontantLigne(BigDecimal montantLigne) { this.montantLigne = montantLigne; }
}
