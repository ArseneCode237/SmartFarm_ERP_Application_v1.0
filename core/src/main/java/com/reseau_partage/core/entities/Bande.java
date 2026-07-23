package com.reseau_partage.core.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "bandes")
public class Bande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code_bande", nullable = false, unique = true, length = 30)
    private String codeBande;

    @Column(nullable = false, length = 100)
    private String nom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Espece espece;

    @Column(length = 100)
    private String race;

    @Column(length = 50)
    private String souche;

    @Enumerated(EnumType.STRING)
    @Column
    private Categorie categorie;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_production", nullable = false)
    private TypeProduction typeProduction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id")
    private Site site;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "structure_id", nullable = false)
    private Structure structure;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provenance provenance;

    @Column(name = "fournisseur_nom", length = 100)
    private String fournisseurNom;

    @Column(name = "cout_achat_unitaire", precision = 10, scale = 2)
    private BigDecimal coutAchatUnitaire;

    @Column(name = "effectif_initial", nullable = false)
    private Integer effectifInitial;

    @Column(name = "effectif_actuel", nullable = false)
    private Integer effectifActuel;

    @Column(name = "effectif_morts")
    private Integer effectifMorts = 0;

    @Column(name = "effectif_vendus")
    private Integer effectifVendus = 0;

    @Column(name = "effectif_reformes")
    private Integer effectifReformes = 0;

    @Column(name = "date_entree", nullable = false)
    private LocalDate dateEntree;

    @Column(name = "date_sortie_prevue")
    private LocalDate dateSortiePrevue;

    @Column(name = "date_sortie_reelle")
    private LocalDate dateSortieReelle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutBande statut;

    @Column(name = "poids_moyen_entree_kg", precision = 8, scale = 3)
    private BigDecimal poidsMoyenEntreeKg;

    @Column(name = "poids_total_sortie", precision = 10, scale = 3)
    private BigDecimal poidsTotalSortie;

    @Column(name = "poids_moyen_actuel_kg", precision = 8, scale = 3)
    private BigDecimal poidsMoyenActuelKg;

    @Column(name = "fcr_cumule", precision = 5, scale = 3)
    private BigDecimal fcrCumule;

    @Column(name = "taux_ponte_pct", precision = 5, scale = 2)
    private BigDecimal tauxPontePct;

    @Column(name = "gain_moyen_quotidien_g", precision = 8, scale = 3)
    private BigDecimal gainMoyenQuotidienG;

    @Column(name = "ration_journaliere_kg", precision = 8, scale = 3)
    private BigDecimal rationJournaliereKg;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "total_declares_morts")
    private Integer totalDeclaresMorts = 0;

    @Column(name = "total_declares_vendus")
    private Integer totalDeclaresVendus = 0;

    @Column(name = "total_declares_reformes")
    private Integer totalDeclaresReformes = 0;

    @Column(name = "revenu_total_ventes", precision = 15, scale = 2)
    private BigDecimal revenuTotalVentes = BigDecimal.ZERO;

    @Column(name = "date_derniere_declaration")
    private LocalDate dateDerniereDeclaration;

    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @PrePersist
    protected void onCreate() {
        this.dateCreation = LocalDateTime.now();
        this.dateModification = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.dateModification = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCodeBande() { return codeBande; }
    public void setCodeBande(String codeBande) { this.codeBande = codeBande; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public Espece getEspece() { return espece; }
    public void setEspece(Espece espece) { this.espece = espece; }
    public String getRace() { return race; }
    public void setRace(String race) { this.race = race; }
    public String getSouche() { return souche; }
    public void setSouche(String souche) { this.souche = souche; }
    public Categorie getCategorie() { return categorie; }
    public void setCategorie(Categorie categorie) { this.categorie = categorie; }
    public TypeProduction getTypeProduction() { return typeProduction; }
    public void setTypeProduction(TypeProduction typeProduction) { this.typeProduction = typeProduction; }
    public Site getSite() { return site; }
    public void setSite(Site site) { this.site = site; }
    public Structure getStructure() { return structure; }
    public void setStructure(Structure structure) { this.structure = structure; }
    public Provenance getProvenance() { return provenance; }
    public void setProvenance(Provenance provenance) { this.provenance = provenance; }
    public String getFournisseurNom() { return fournisseurNom; }
    public void setFournisseurNom(String fournisseurNom) { this.fournisseurNom = fournisseurNom; }
    public BigDecimal getCoutAchatUnitaire() { return coutAchatUnitaire; }
    public void setCoutAchatUnitaire(BigDecimal coutAchatUnitaire) { this.coutAchatUnitaire = coutAchatUnitaire; }
    public Integer getEffectifInitial() { return effectifInitial; }
    public void setEffectifInitial(Integer effectifInitial) { this.effectifInitial = effectifInitial; }
    public Integer getEffectifActuel() { return effectifActuel; }
    public void setEffectifActuel(Integer effectifActuel) { this.effectifActuel = effectifActuel; }
    public Integer getEffectifMorts() { return effectifMorts; }
    public void setEffectifMorts(Integer effectifMorts) { this.effectifMorts = effectifMorts; }
    public Integer getEffectifVendus() { return effectifVendus; }
    public void setEffectifVendus(Integer effectifVendus) { this.effectifVendus = effectifVendus; }
    public Integer getEffectifReformes() { return effectifReformes; }
    public void setEffectifReformes(Integer effectifReformes) { this.effectifReformes = effectifReformes; }
    public LocalDate getDateEntree() { return dateEntree; }
    public void setDateEntree(LocalDate dateEntree) { this.dateEntree = dateEntree; }
    public LocalDate getDateSortiePrevue() { return dateSortiePrevue; }
    public void setDateSortiePrevue(LocalDate dateSortiePrevue) { this.dateSortiePrevue = dateSortiePrevue; }
    public LocalDate getDateSortieReelle() { return dateSortieReelle; }
    public void setDateSortieReelle(LocalDate dateSortieReelle) { this.dateSortieReelle = dateSortieReelle; }
    public StatutBande getStatut() { return statut; }
    public void setStatut(StatutBande statut) { this.statut = statut; }
    public BigDecimal getPoidsMoyenEntreeKg() { return poidsMoyenEntreeKg; }
    public void setPoidsMoyenEntreeKg(BigDecimal poidsMoyenEntreeKg) { this.poidsMoyenEntreeKg = poidsMoyenEntreeKg; }
    public BigDecimal getPoidsTotalSortie() { return poidsTotalSortie; }
    public void setPoidsTotalSortie(BigDecimal poidsTotalSortie) { this.poidsTotalSortie = poidsTotalSortie; }
    public BigDecimal getPoidsMoyenActuelKg() { return poidsMoyenActuelKg; }
    public void setPoidsMoyenActuelKg(BigDecimal poidsMoyenActuelKg) { this.poidsMoyenActuelKg = poidsMoyenActuelKg; }
    public BigDecimal getFcrCumule() { return fcrCumule; }
    public void setFcrCumule(BigDecimal fcrCumule) { this.fcrCumule = fcrCumule; }
    public BigDecimal getTauxPontePct() { return tauxPontePct; }
    public void setTauxPontePct(BigDecimal tauxPontePct) { this.tauxPontePct = tauxPontePct; }
    public BigDecimal getGainMoyenQuotidienG() { return gainMoyenQuotidienG; }
    public void setGainMoyenQuotidienG(BigDecimal gainMoyenQuotidienG) { this.gainMoyenQuotidienG = gainMoyenQuotidienG; }
    public BigDecimal getRationJournaliereKg() { return rationJournaliereKg; }
    public void setRationJournaliereKg(BigDecimal rationJournaliereKg) { this.rationJournaliereKg = rationJournaliereKg; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public LocalDateTime getDateModification() { return dateModification; }
    public void setDateModification(LocalDateTime dateModification) { this.dateModification = dateModification; }

    // Nouveaux champs pour les déclarations
    public Integer getTotalDeclaresMorts() { return totalDeclaresMorts; }
    public void setTotalDeclaresMorts(Integer totalDeclaresMorts) { this.totalDeclaresMorts = totalDeclaresMorts; }
    public Integer getTotalDeclaresVendus() { return totalDeclaresVendus; }
    public void setTotalDeclaresVendus(Integer totalDeclaresVendus) { this.totalDeclaresVendus = totalDeclaresVendus; }
    public Integer getTotalDeclaresReformes() { return totalDeclaresReformes; }
    public void setTotalDeclaresReformes(Integer totalDeclaresReformes) { this.totalDeclaresReformes = totalDeclaresReformes; }
    public BigDecimal getRevenuTotalVentes() { return revenuTotalVentes; }
    public void setRevenuTotalVentes(BigDecimal revenuTotalVentes) { this.revenuTotalVentes = revenuTotalVentes; }
    public LocalDate getDateDerniereDeclaration() { return dateDerniereDeclaration; }
    public void setDateDerniereDeclaration(LocalDate dateDerniereDeclaration) { this.dateDerniereDeclaration = dateDerniereDeclaration; }
}
