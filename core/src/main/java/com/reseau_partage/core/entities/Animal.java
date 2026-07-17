package com.reseau_partage.core.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "animaux")
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code_unique", nullable = false, unique = true, length = 30)
    private String codeUnique;

    @Column(name = "code_rfid", length = 50)
    private String codeRfid;

    @Column(name = "code_boucle", length = 30)
    private String codeBoucle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Espece espece;

    @Column(length = 100)
    private String race;

    @Column(length = 50)
    private String souche;

    @Enumerated(EnumType.STRING)
    private Sexe sexe;

    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    @Column(name = "date_entree", nullable = false)
    private LocalDate dateEntree;

    @Column(name = "poids_entree_kg", precision = 8, scale = 3)
    private BigDecimal poidsEntreeKg;

    @Column(name = "poids_actuel_kg", precision = 8, scale = 3)
    private BigDecimal poidsActuelKg;

    @Column(name = "date_derniere_pesee")
    private LocalDate dateDernierePesee;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode_suivi", nullable = false)
    private ModeSuivi modeSuivi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bande_id")
    private Bande bande;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "structure_id")
    private Structure structure;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mere_id")
    private Animal mere;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pere_id")
    private Animal pere;

    @Enumerated(EnumType.STRING)
    private Provenance provenance;

    @Column(name = "fournisseur_nom", length = 150)
    private String fournisseurNom;

    @Column(name = "numero_lot_achat", length = 50)
    private String numeroLotAchat;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutAnimal statut;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_reproducteur")
    private StatutReproducteur statutReproducteur;

    @Column(name = "date_sortie")
    private LocalDate dateSortie;

    @Column(name = "motif_sortie", length = 200)
    private String motifSortie;

    @Column(name = "cause_mort", length = 200)
    private String causeMort;

    @Column(columnDefinition = "TEXT")
    private String notes;

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
    public String getCodeUnique() { return codeUnique; }
    public void setCodeUnique(String codeUnique) { this.codeUnique = codeUnique; }
    public String getCodeRfid() { return codeRfid; }
    public void setCodeRfid(String codeRfid) { this.codeRfid = codeRfid; }
    public String getCodeBoucle() { return codeBoucle; }
    public void setCodeBoucle(String codeBoucle) { this.codeBoucle = codeBoucle; }
    public Espece getEspece() { return espece; }
    public void setEspece(Espece espece) { this.espece = espece; }
    public String getRace() { return race; }
    public void setRace(String race) { this.race = race; }
    public String getSouche() { return souche; }
    public void setSouche(String souche) { this.souche = souche; }
    public Sexe getSexe() { return sexe; }
    public void setSexe(Sexe sexe) { this.sexe = sexe; }
    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }
    public LocalDate getDateEntree() { return dateEntree; }
    public void setDateEntree(LocalDate dateEntree) { this.dateEntree = dateEntree; }
    public BigDecimal getPoidsEntreeKg() { return poidsEntreeKg; }
    public void setPoidsEntreeKg(BigDecimal poidsEntreeKg) { this.poidsEntreeKg = poidsEntreeKg; }
    public BigDecimal getPoidsActuelKg() { return poidsActuelKg; }
    public void setPoidsActuelKg(BigDecimal poidsActuelKg) { this.poidsActuelKg = poidsActuelKg; }
    public LocalDate getDateDernierePesee() { return dateDernierePesee; }
    public void setDateDernierePesee(LocalDate dateDernierePesee) { this.dateDernierePesee = dateDernierePesee; }
    public ModeSuivi getModeSuivi() { return modeSuivi; }
    public void setModeSuivi(ModeSuivi modeSuivi) { this.modeSuivi = modeSuivi; }
    public Bande getBande() { return bande; }
    public void setBande(Bande bande) { this.bande = bande; }
    public Structure getStructure() { return structure; }
    public void setStructure(Structure structure) { this.structure = structure; }
    public Animal getMere() { return mere; }
    public void setMere(Animal mere) { this.mere = mere; }
    public Animal getPere() { return pere; }
    public void setPere(Animal pere) { this.pere = pere; }
    public Provenance getProvenance() { return provenance; }
    public void setProvenance(Provenance provenance) { this.provenance = provenance; }
    public String getFournisseurNom() { return fournisseurNom; }
    public void setFournisseurNom(String fournisseurNom) { this.fournisseurNom = fournisseurNom; }
    public String getNumeroLotAchat() { return numeroLotAchat; }
    public void setNumeroLotAchat(String numeroLotAchat) { this.numeroLotAchat = numeroLotAchat; }
    public StatutAnimal getStatut() { return statut; }
    public void setStatut(StatutAnimal statut) { this.statut = statut; }
    public StatutReproducteur getStatutReproducteur() { return statutReproducteur; }
    public void setStatutReproducteur(StatutReproducteur statutReproducteur) { this.statutReproducteur = statutReproducteur; }
    public LocalDate getDateSortie() { return dateSortie; }
    public void setDateSortie(LocalDate dateSortie) { this.dateSortie = dateSortie; }
    public String getMotifSortie() { return motifSortie; }
    public void setMotifSortie(String motifSortie) { this.motifSortie = motifSortie; }
    public String getCauseMort() { return causeMort; }
    public void setCauseMort(String causeMort) { this.causeMort = causeMort; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public LocalDateTime getDateModification() { return dateModification; }
    public void setDateModification(LocalDateTime dateModification) { this.dateModification = dateModification; }
}
