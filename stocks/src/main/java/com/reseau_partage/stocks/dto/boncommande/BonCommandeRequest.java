package com.reseau_partage.stocks.dto.boncommande;

import com.reseau_partage.core.entities.enumtypes.StatutBonCommande;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BonCommandeRequest {

    private Long fournisseurId;

    @NotNull
    private Long fermeId;

    private List<LigneBonCommandeRequest> lignes;

    private LocalDate dateCommande;

    private LocalDate dateLivraisonPrevue;

    private String notes;

    public Long getFournisseurId() {
        return fournisseurId;
    }

    public void setFournisseurId(Long fournisseurId) {
        this.fournisseurId = fournisseurId;
    }

    public Long getFermeId() {
        return fermeId;
    }

    public void setFermeId(Long fermeId) {
        this.fermeId = fermeId;
    }

    public List<LigneBonCommandeRequest> getLignes() {
        return lignes;
    }

    public void setLignes(List<LigneBonCommandeRequest> lignes) {
        this.lignes = lignes;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}