package com.reseau_partage.core.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "config_especes")
public class ConfigEspece {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private Espece espece;

    private Integer dureeGestationJours;
    private Integer ageMaturiteSexuelleJours;
    private Integer intervalleEntreGestationsJours;
    private Integer dureeSevrageJours;
    private Integer taillePorteeMoyenne;

    private BigDecimal poidsNaissanceMoyenKg;
    private BigDecimal poidsAbattageCibleKg;
    private Integer ageCibleAbattageJours;
    private BigDecimal fcrCibleMoyen;
    private BigDecimal gmqCibleG;

    private Integer intervalleVaccinationJours;
    private Integer dureeQuarantaineJours;
    private Integer seuilAlerteMortalitePct;

    private BigDecimal tauxPonteCiblePct;
    private Integer dureeProductionLaitJours;
    private BigDecimal productionLaitJournaliereLitres;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Espece getEspece() { return espece; }
    public void setEspece(Espece espece) { this.espece = espece; }
    public Integer getDureeGestationJours() { return dureeGestationJours; }
    public void setDureeGestationJours(Integer dureeGestationJours) { this.dureeGestationJours = dureeGestationJours; }
    public Integer getAgeMaturiteSexuelleJours() { return ageMaturiteSexuelleJours; }
    public void setAgeMaturiteSexuelleJours(Integer ageMaturiteSexuelleJours) { this.ageMaturiteSexuelleJours = ageMaturiteSexuelleJours; }
    public Integer getIntervalleEntreGestationsJours() { return intervalleEntreGestationsJours; }
    public void setIntervalleEntreGestationsJours(Integer intervalleEntreGestationsJours) { this.intervalleEntreGestationsJours = intervalleEntreGestationsJours; }
    public Integer getDureeSevrageJours() { return dureeSevrageJours; }
    public void setDureeSevrageJours(Integer dureeSevrageJours) { this.dureeSevrageJours = dureeSevrageJours; }
    public Integer getTaillePorteeMoyenne() { return taillePorteeMoyenne; }
    public void setTaillePorteeMoyenne(Integer taillePorteeMoyenne) { this.taillePorteeMoyenne = taillePorteeMoyenne; }
    public BigDecimal getPoidsNaissanceMoyenKg() { return poidsNaissanceMoyenKg; }
    public void setPoidsNaissanceMoyenKg(BigDecimal poidsNaissanceMoyenKg) { this.poidsNaissanceMoyenKg = poidsNaissanceMoyenKg; }
    public BigDecimal getPoidsAbattageCibleKg() { return poidsAbattageCibleKg; }
    public void setPoidsAbattageCibleKg(BigDecimal poidsAbattageCibleKg) { this.poidsAbattageCibleKg = poidsAbattageCibleKg; }
    public Integer getAgeCibleAbattageJours() { return ageCibleAbattageJours; }
    public void setAgeCibleAbattageJours(Integer ageCibleAbattageJours) { this.ageCibleAbattageJours = ageCibleAbattageJours; }
    public BigDecimal getFcrCibleMoyen() { return fcrCibleMoyen; }
    public void setFcrCibleMoyen(BigDecimal fcrCibleMoyen) { this.fcrCibleMoyen = fcrCibleMoyen; }
    public BigDecimal getGmqCibleG() { return gmqCibleG; }
    public void setGmqCibleG(BigDecimal gmqCibleG) { this.gmqCibleG = gmqCibleG; }
    public Integer getIntervalleVaccinationJours() { return intervalleVaccinationJours; }
    public void setIntervalleVaccinationJours(Integer intervalleVaccinationJours) { this.intervalleVaccinationJours = intervalleVaccinationJours; }
    public Integer getDureeQuarantaineJours() { return dureeQuarantaineJours; }
    public void setDureeQuarantaineJours(Integer dureeQuarantaineJours) { this.dureeQuarantaineJours = dureeQuarantaineJours; }
    public Integer getSeuilAlerteMortalitePct() { return seuilAlerteMortalitePct; }
    public void setSeuilAlerteMortalitePct(Integer seuilAlerteMortalitePct) { this.seuilAlerteMortalitePct = seuilAlerteMortalitePct; }
    public BigDecimal getTauxPonteCiblePct() { return tauxPonteCiblePct; }
    public void setTauxPonteCiblePct(BigDecimal tauxPonteCiblePct) { this.tauxPonteCiblePct = tauxPonteCiblePct; }
    public Integer getDureeProductionLaitJours() { return dureeProductionLaitJours; }
    public void setDureeProductionLaitJours(Integer dureeProductionLaitJours) { this.dureeProductionLaitJours = dureeProductionLaitJours; }
    public BigDecimal getProductionLaitJournaliereLitres() { return productionLaitJournaliereLitres; }
    public void setProductionLaitJournaliereLitres(BigDecimal productionLaitJournaliereLitres) { this.productionLaitJournaliereLitres = productionLaitJournaliereLitres; }
}
