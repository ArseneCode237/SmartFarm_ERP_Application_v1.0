package com.reseau_partage.core.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "velages_bovins")
public class VelageBovin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "saillie_id", nullable = false, unique = true)
    private SaillieBovin saillie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vache_id", nullable = false)
    private Animal vache;

    @Column(name = "numero_velage", nullable = false)
    private Integer numeroVelage;

    @Column(name = "date_velage_reel", nullable = false)
    private LocalDateTime dateVelageReel;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_velage")
    private TypeVelage typeVelage;

    @Column(name = "nb_veaux_vivants", nullable = false)
    private Integer nbVeauxVivants;

    @Column(name = "nb_veaux_mort_nes", nullable = false)
    private Integer nbVeauxMortNes = 0;

    @Column(name = "poids_naissance_kg", precision = 5, scale = 1)
    private BigDecimal poidsNaissanceKg;

    @Column(name = "retention_placenta", nullable = false)
    private Boolean retentionPlacenta = false;

    @Column(name = "fievre_de_lait", nullable = false)
    private Boolean fievreDeLait = false;

    @Column(name = "date_debut_lactation")
    private LocalDate dateDebutLactation;

    @Column(name = "veau_id")
    private Long veauId;

    @ElementCollection
    @CollectionTable(name = "velages_bovins_veaux", joinColumns = @JoinColumn(name = "velage_id"))
    @Column(name = "veau_id", nullable = false)
    private List<Long> veauIds = new ArrayList<>();

    @Column(name = "veterinaire_nom", length = 100)
    private String veterinaireNom;

    @Column(name = "operateur_nom", length = 100)
    private String operateurNom;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public SaillieBovin getSaillie() { return saillie; }
    public void setSaillie(SaillieBovin value) { saillie = value; }
    public Animal getVache() { return vache; }
    public void setVache(Animal value) { vache = value; }
    public Integer getNumeroVelage() { return numeroVelage; }
    public void setNumeroVelage(Integer value) { numeroVelage = value; }
    public LocalDateTime getDateVelageReel() { return dateVelageReel; }
    public void setDateVelageReel(LocalDateTime value) { dateVelageReel = value; }
    public TypeVelage getTypeVelage() { return typeVelage; }
    public void setTypeVelage(TypeVelage value) { typeVelage = value; }
    public Integer getNbVeauxVivants() { return nbVeauxVivants; }
    public void setNbVeauxVivants(Integer value) { nbVeauxVivants = value; }
    public Integer getNbVeauxMortNes() { return nbVeauxMortNes; }
    public void setNbVeauxMortNes(Integer value) { nbVeauxMortNes = value; }
    public BigDecimal getPoidsNaissanceKg() { return poidsNaissanceKg; }
    public void setPoidsNaissanceKg(BigDecimal value) { poidsNaissanceKg = value; }
    public Boolean getRetentionPlacenta() { return retentionPlacenta; }
    public void setRetentionPlacenta(Boolean value) { retentionPlacenta = value; }
    public Boolean getFievreDeLait() { return fievreDeLait; }
    public void setFievreDeLait(Boolean value) { fievreDeLait = value; }
    public LocalDate getDateDebutLactation() { return dateDebutLactation; }
    public void setDateDebutLactation(LocalDate value) { dateDebutLactation = value; }
    public Long getVeauId() { return veauId; }
    public void setVeauId(Long value) { veauId = value; }
    public List<Long> getVeauIds() { return veauIds; }
    public void setVeauIds(List<Long> value) { veauIds = value; }
    public String getVeterinaireNom() { return veterinaireNom; }
    public void setVeterinaireNom(String value) { veterinaireNom = value; }
    public String getOperateurNom() { return operateurNom; }
    public void setOperateurNom(String value) { operateurNom = value; }
    public String getNotes() { return notes; }
    public void setNotes(String value) { notes = value; }
}
