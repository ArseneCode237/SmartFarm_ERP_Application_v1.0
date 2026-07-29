package com.reseau_partage.core.entities;

import com.reseau_partage.core.entities.enumtypes.StatutBonCommande;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bons_commande")
public class BonCommande {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_bc", nullable = false, unique = true, length = 30)
    private String numeroBc;

    @Column(name = "ferme_id", nullable = false)
    private Long fermeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fournisseur_id", nullable = false)
    private Fournisseur fournisseur;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutBonCommande statut = StatutBonCommande.BROUILLON;

    @Column(name = "date_commande")
    private LocalDate dateCommande;

    @Column(name = "date_livraison_prevue")
    private LocalDate dateLivraisonPrevue;

    @Column(name = "date_livraison_reelle")
    private LocalDate dateLivraisonReelle;

    @Column(name = "montant_total_ht", precision = 15, scale = 2)
    private BigDecimal montantTotalHt;

    @OneToMany(mappedBy = "bonCommande", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LigneBonCommande> lignes = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    public BonCommande() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroBc() {
        return numeroBc;
    }

    public void setNumeroBc(String numeroBc) {
        this.numeroBc = numeroBc;
    }

    public Long getFermeId() {
        return fermeId;
    }

    public void setFermeId(Long fermeId) {
        this.fermeId = fermeId;
    }

    public Fournisseur getFournisseur() {
        return fournisseur;
    }

    public void setFournisseur(Fournisseur fournisseur) {
        this.fournisseur = fournisseur;
    }

    public StatutBonCommande getStatut() {
        return statut;
    }

    public void setStatut(StatutBonCommande statut) {
        this.statut = statut;
    }

    public LocalDate getDateCommande() {
        return dateCommande;
    }

    public void setDateCommande(LocalDate dateCommande) {
        this.dateCommande = dateCommande;
    }

    public LocalDate getDateLivraisonPrevue() {
        return dateLivraisonPrevue;
    }

    public void setDateLivraisonPrevue(LocalDate dateLivraisonPrevue) {
        this.dateLivraisonPrevue = dateLivraisonPrevue;
    }

    public LocalDate getDateLivraisonReelle() {
        return dateLivraisonReelle;
    }

    public void setDateLivraisonReelle(LocalDate dateLivraisonReelle) {
        this.dateLivraisonReelle = dateLivraisonReelle;
    }

    public BigDecimal getMontantTotalHt() {
        return montantTotalHt;
    }

    public void setMontantTotalHt(BigDecimal montantTotalHt) {
        this.montantTotalHt = montantTotalHt;
    }

    public List<LigneBonCommande> getLignes() {
        return lignes;
    }

    public void setLignes(List<LigneBonCommande> lignes) {
        this.lignes = lignes;
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