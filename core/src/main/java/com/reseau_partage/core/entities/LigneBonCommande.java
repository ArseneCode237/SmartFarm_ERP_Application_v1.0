package com.reseau_partage.core.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "lignes_bon_commande")
public class LigneBonCommande {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bon_commande_id", nullable = false)
    private BonCommande bonCommande;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal quantiteCommandee;

    @Column(name = "quantite_recue", precision = 12, scale = 3)
    private BigDecimal quantiteRecue = BigDecimal.ZERO;

    @Column(name = "prix_unitaire", precision = 15, scale = 2)
    private BigDecimal prixUnitaire;

    @Column(name = "montant_ligne", precision = 15, scale = 2)
    private BigDecimal montantLigne;

    public LigneBonCommande() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BonCommande getBonCommande() {
        return bonCommande;
    }

    public void setBonCommande(BonCommande bonCommande) {
        this.bonCommande = bonCommande;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public BigDecimal getQuantiteCommandee() {
        return quantiteCommandee;
    }

    public void setQuantiteCommandee(BigDecimal quantiteCommandee) {
        this.quantiteCommandee = quantiteCommandee;
    }

    public BigDecimal getQuantiteRecue() {
        return quantiteRecue;
    }

    public void setQuantiteRecue(BigDecimal quantiteRecue) {
        this.quantiteRecue = quantiteRecue;
    }

    public BigDecimal getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(BigDecimal prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public BigDecimal getMontantLigne() {
        return montantLigne;
    }

    public void setMontantLigne(BigDecimal montantLigne) {
        this.montantLigne = montantLigne;
    }
}