package com.reseau_partage.core.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "depots")
public class Depot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String structureId;

    @Column(nullable = false)
    private Integer mois;

    @Column(nullable = false)
    private Integer annee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutDepot statut;

    @Column(nullable = false)
    private LocalDateTime dateDepot;

    private LocalDateTime datePriseEnCharge;

    @Column(nullable = false)
    private Long deposantId;

    private Long prisEnChargeParId;

    @OneToMany(mappedBy = "depot", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FichierDepot> fichiers = new ArrayList<>();

    public Depot() {}

    public Depot(Long id, String structureId, Integer mois, Integer annee, StatutDepot statut, 
                 LocalDateTime dateDepot, LocalDateTime datePriseEnCharge, Long deposantId, 
                 Long prisEnChargeParId, List<FichierDepot> fichiers) {
        this.id = id;
        this.structureId = structureId;
        this.mois = mois;
        this.annee = annee;
        this.statut = statut;
        this.dateDepot = dateDepot;
        this.datePriseEnCharge = datePriseEnCharge;
        this.deposantId = deposantId;
        this.prisEnChargeParId = prisEnChargeParId;
        this.fichiers = fichiers != null ? fichiers : new ArrayList<>();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStructureId() { return structureId; }
    public void setStructureId(String structureId) { this.structureId = structureId; }

    public Integer getMois() { return mois; }
    public void setMois(Integer mois) { this.mois = mois; }

    public Integer getAnnee() { return annee; }
    public void setAnnee(Integer annee) { this.annee = annee; }

    public StatutDepot getStatut() { return statut; }
    public void setStatut(StatutDepot statut) { this.statut = statut; }

    public LocalDateTime getDateDepot() { return dateDepot; }
    public void setDateDepot(LocalDateTime dateDepot) { this.dateDepot = dateDepot; }

    public LocalDateTime getDatePriseEnCharge() { return datePriseEnCharge; }
    public void setDatePriseEnCharge(LocalDateTime datePriseEnCharge) { this.datePriseEnCharge = datePriseEnCharge; }

    public Long getDeposantId() { return deposantId; }
    public void setDeposantId(Long deposantId) { this.deposantId = deposantId; }

    public Long getPrisEnChargeParId() { return prisEnChargeParId; }
    public void setPrisEnChargeParId(Long prisEnChargeParId) { this.prisEnChargeParId = prisEnChargeParId; }

    public List<FichierDepot> getFichiers() { return fichiers; }
    public void setFichiers(List<FichierDepot> fichiers) { this.fichiers = fichiers; }

    // Manual Builder
    public static DepotBuilder builder() { return new DepotBuilder(); }

    public static class DepotBuilder {
        private Long id;
        private String structureId;
        private Integer mois;
        private Integer annee;
        private StatutDepot statut;
        private LocalDateTime dateDepot;
        private LocalDateTime datePriseEnCharge;
        private Long deposantId;
        private Long prisEnChargeParId;
        private List<FichierDepot> fichiers = new ArrayList<>();

        public DepotBuilder id(Long id) { this.id = id; return this; }
        public DepotBuilder structureId(String structureId) { this.structureId = structureId; return this; }
        public DepotBuilder mois(Integer mois) { this.mois = mois; return this; }
        public DepotBuilder annee(Integer annee) { this.annee = annee; return this; }
        public DepotBuilder statut(StatutDepot statut) { this.statut = statut; return this; }
        public DepotBuilder dateDepot(LocalDateTime dateDepot) { this.dateDepot = dateDepot; return this; }
        public DepotBuilder datePriseEnCharge(LocalDateTime datePriseEnCharge) { this.datePriseEnCharge = datePriseEnCharge; return this; }
        public DepotBuilder deposantId(Long deposantId) { this.deposantId = deposantId; return this; }
        public DepotBuilder prisEnChargeParId(Long prisEnChargeParId) { this.prisEnChargeParId = prisEnChargeParId; return this; }
        public DepotBuilder fichiers(List<FichierDepot> fichiers) { this.fichiers = fichiers; return this; }

        public Depot build() {
            return new Depot(id, structureId, mois, annee, statut, dateDepot, datePriseEnCharge, deposantId, prisEnChargeParId, fichiers);
        }
    }
}
