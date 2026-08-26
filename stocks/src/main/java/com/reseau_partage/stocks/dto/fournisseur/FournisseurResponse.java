package com.reseau_partage.stocks.dto.fournisseur;

import com.reseau_partage.core.entities.enumtypes.CategorieArticle;

import java.time.LocalDateTime;
import java.util.List;

public class FournisseurResponse {

    private Long id;
    private Long fermeId;
    private String nom;
    private String adresse;
    private String ville;
    private String telephone;
    private String email;
    private String personneContact;
    private List<CategorieArticle> categoriesFournies;
    private Integer noteQualite;
    private Integer delaiLivraisonJours;
    private String conditionsPaiement;
    private Boolean actif;
    private LocalDateTime dateCreation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFermeId() {
        return fermeId;
    }

    public void setFermeId(Long fermeId) {
        this.fermeId = fermeId;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPersonneContact() {
        return personneContact;
    }

    public void setPersonneContact(String personneContact) {
        this.personneContact = personneContact;
    }

    public List<CategorieArticle> getCategoriesFournies() {
        return categoriesFournies;
    }

    public void setCategoriesFournies(List<CategorieArticle> categoriesFournies) {
        this.categoriesFournies = categoriesFournies;
    }

    public Integer getNoteQualite() {
        return noteQualite;
    }

    public void setNoteQualite(Integer noteQualite) {
        this.noteQualite = noteQualite;
    }

    public Integer getDelaiLivraisonJours() {
        return delaiLivraisonJours;
    }

    public void setDelaiLivraisonJours(Integer delaiLivraisonJours) {
        this.delaiLivraisonJours = delaiLivraisonJours;
    }

    public String getConditionsPaiement() {
        return conditionsPaiement;
    }

    public void setConditionsPaiement(String conditionsPaiement) {
        this.conditionsPaiement = conditionsPaiement;
    }

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
}