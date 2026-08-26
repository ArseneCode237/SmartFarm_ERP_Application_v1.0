package com.reseau_partage.core.entities;

import com.reseau_partage.core.entities.enumtypes.CategorieArticle;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "fournisseurs")
public class Fournisseur {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ferme_id", nullable = false)
    private Long fermeId;

    @Column(nullable = false, length = 200)
    private String nom;

    @Column(length = 200)
    private String adresse;

    @Column(length = 100)
    private String ville;

    @Column(length = 20)
    private String telephone;

    @Column(length = 150)
    private String email;

    @Column(name = "personne_contact", length = 150)
    private String personneContact;

    @ElementCollection
    @CollectionTable(
        name = "fournisseur_categories",
        joinColumns = @JoinColumn(name = "fournisseur_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "categorie")
    private List<CategorieArticle> categoriesFournies;

    @Column(name = "note_qualite")
    private Integer noteQualite;

    @Column(name = "delai_livraison_jours")
    private Integer delaiLivraisonJours;

    @Column(name = "conditions_paiement", length = 200)
    private String conditionsPaiement;

    @Column(name = "actif", nullable = false)
    private Boolean actif = true;

    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    public Fournisseur() {
    }

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

    @PrePersist
    protected void onCreate() {
        this.dateCreation = LocalDateTime.now();
    }
}