package com.reseau_partage.stocks.dto.boncommande;

import com.reseau_partage.core.entities.enumtypes.StatutBonCommande;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class BonCommandeResponse {

    private Long id;
    private String numeroBc;
    private Long fermeId;
    private Long fournisseurId;
    private String fournisseurNom;
    private StatutBonCommande statut;
    private LocalDate dateCommande;
    private LocalDate dateLivraisonPrevue;
    private LocalDate dateLivraisonReelle;
    private BigDecimal montantTotalHt;
    private List<LigneBonCommandeResponse> lignes;
    private String notes;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;

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

    public Long getFournisseurId() {
        return fournisseurId;
    }

    public void setFournisseurId(Long fournisseurId) {
        this.fournisseurId = fournisseurId;
    }

    public String getFournisseurNom() {
        return fournisseurNom;
    }

    public void setFournisseurNom(String fournisseurNom) {
        this.fournisseurNom = fournisseurNom;
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

    public List<LigneBonCommandeResponse> getLignes() {
        return lignes;
    }

    public void setLignes(List<LigneBonCommandeResponse> lignes) {
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
}