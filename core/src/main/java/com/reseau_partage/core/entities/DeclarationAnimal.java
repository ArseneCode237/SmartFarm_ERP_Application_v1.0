package com.reseau_partage.core.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "declarations_animaux", indexes = {
        @Index(name = "idx_decl_animal_id", columnList = "animal_id"),
        @Index(name = "idx_decl_animal_ferme_id", columnList = "ferme_id"),
        @Index(name = "idx_decl_animal_type", columnList = "type"),
        @Index(name = "idx_decl_animal_date", columnList = "date_declaration"),
        @Index(name = "idx_decl_animal_statut", columnList = "statut")
})
public class DeclarationAnimal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "animal_id", nullable = false)
    private Long animalId;

    @Column(name = "animal_code_unique", length = 30)
    private String animalCodeUnique;

    @Column(name = "animal_code_boucle", length = 30)
    private String animalCodeBoucle;

    @Column(name = "espece", length = 30)
    private String espece;

    @Column(name = "ferme_id", nullable = false)
    private Long fermeId;

    @Column(name = "utilisateur_id")
    private Long utilisateurId;

    @Column(name = "utilisateur_nom", length = 150)
    private String utilisateurNom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TypeDeclaration type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private MotifDeclaration motif;

    @Column(name = "date_declaration", nullable = false)
    private LocalDate dateDeclaration;

    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @Column(name = "poids_kg", precision = 8, scale = 3)
    private BigDecimal poidsKg;

    @Column(name = "prix_par_kg")
    private Boolean prixParKg = false;

    @Column(name = "prix_unitaire", precision = 15, scale = 2)
    private BigDecimal prixUnitaire;

    @Column(name = "montant_total", precision = 15, scale = 2)
    private BigDecimal montantTotal;

    @Column(name = "nom_acheteur", length = 255)
    private String nomAcheteur;

    @Column(name = "telephone_acheteur", length = 20)
    private String telephoneAcheteur;

    @Column(name = "localite_acheteur", length = 150)
    private String localiteAcheteur;

    @Column(columnDefinition = "TEXT")
    private String observations;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private SourceDeclaration source = SourceDeclaration.MANUEL;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private StatutDeclaration statut = StatutDeclaration.ACTIF;

    @Column(name = "motif_annulation", length = 200)
    private String motifAnnulation;

    @Column(name = "date_annulation")
    private LocalDateTime dateAnnulation;

    @Column(name = "utilisateur_annulation_id")
    private Long utilisateurAnnulationId;

    @PrePersist
    protected void onCreate() {
        this.dateCreation = LocalDateTime.now();
        this.dateModification = LocalDateTime.now();
        if (this.type == TypeDeclaration.VENTE && this.prixUnitaire != null) {
            this.montantTotal = this.prixUnitaire;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.dateModification = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getAnimalId() { return animalId; }
    public void setAnimalId(Long animalId) { this.animalId = animalId; }
    public String getAnimalCodeUnique() { return animalCodeUnique; }
    public void setAnimalCodeUnique(String animalCodeUnique) { this.animalCodeUnique = animalCodeUnique; }
    public String getAnimalCodeBoucle() { return animalCodeBoucle; }
    public void setAnimalCodeBoucle(String animalCodeBoucle) { this.animalCodeBoucle = animalCodeBoucle; }
    public String getEspece() { return espece; }
    public void setEspece(String espece) { this.espece = espece; }
    public Long getFermeId() { return fermeId; }
    public void setFermeId(Long fermeId) { this.fermeId = fermeId; }
    public Long getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(Long utilisateurId) { this.utilisateurId = utilisateurId; }
    public String getUtilisateurNom() { return utilisateurNom; }
    public void setUtilisateurNom(String utilisateurNom) { this.utilisateurNom = utilisateurNom; }
    public TypeDeclaration getType() { return type; }
    public void setType(TypeDeclaration type) { this.type = type; }
    public MotifDeclaration getMotif() { return motif; }
    public void setMotif(MotifDeclaration motif) { this.motif = motif; }
    public LocalDate getDateDeclaration() { return dateDeclaration; }
    public void setDateDeclaration(LocalDate dateDeclaration) { this.dateDeclaration = dateDeclaration; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public LocalDateTime getDateModification() { return dateModification; }
    public void setDateModification(LocalDateTime dateModification) { this.dateModification = dateModification; }
    public BigDecimal getPoidsKg() { return poidsKg; }
    public void setPoidsKg(BigDecimal poidsKg) { this.poidsKg = poidsKg; }
    public Boolean getPrixParKg() { return prixParKg; }
    public void setPrixParKg(Boolean prixParKg) { this.prixParKg = prixParKg; }
    public BigDecimal getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(BigDecimal prixUnitaire) { this.prixUnitaire = prixUnitaire; }
    public BigDecimal getMontantTotal() { return montantTotal; }
    public void setMontantTotal(BigDecimal montantTotal) { this.montantTotal = montantTotal; }
    public String getNomAcheteur() { return nomAcheteur; }
    public void setNomAcheteur(String nomAcheteur) { this.nomAcheteur = nomAcheteur; }
    public String getTelephoneAcheteur() { return telephoneAcheteur; }
    public void setTelephoneAcheteur(String telephoneAcheteur) { this.telephoneAcheteur = telephoneAcheteur; }
    public String getLocaliteAcheteur() { return localiteAcheteur; }
    public void setLocaliteAcheteur(String localiteAcheteur) { this.localiteAcheteur = localiteAcheteur; }
    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }
    public SourceDeclaration getSource() { return source; }
    public void setSource(SourceDeclaration source) { this.source = source; }
    public StatutDeclaration getStatut() { return statut; }
    public void setStatut(StatutDeclaration statut) { this.statut = statut; }
    public String getMotifAnnulation() { return motifAnnulation; }
    public void setMotifAnnulation(String motifAnnulation) { this.motifAnnulation = motifAnnulation; }
    public LocalDateTime getDateAnnulation() { return dateAnnulation; }
    public void setDateAnnulation(LocalDateTime dateAnnulation) { this.dateAnnulation = dateAnnulation; }
    public Long getUtilisateurAnnulationId() { return utilisateurAnnulationId; }
    public void setUtilisateurAnnulationId(Long utilisateurAnnulationId) { this.utilisateurAnnulationId = utilisateurAnnulationId; }
}
