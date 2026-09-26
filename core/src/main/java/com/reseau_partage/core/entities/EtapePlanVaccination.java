package com.reseau_partage.core.entities;

import java.math.BigDecimal;

import com.reseau_partage.core.entities.enumtypes.VoieAdministration;

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
import jakarta.persistence.Table;

@Entity
@Table(name = "etapes_plan_vaccination")
public class EtapePlanVaccination {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "plan_id", nullable = false) private PlanVaccination plan;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "vaccin_id", nullable = false) private Vaccin vaccin;
    @Column(name = "ordre_etape", nullable = false) private Integer ordreEtape;
    @Column(name = "age_cible_jours", nullable = false) private Integer ageCibleJours;
    @Column(name = "tolerance_jours") private Integer toleranceJours = 3;
    @Column(name = "dose_ml", precision = 6, scale = 2) private BigDecimal doseMl;
    @Enumerated(EnumType.STRING) @Column(name = "voie_administration") private VoieAdministration voieAdministration;
    @Column(length = 200) private String instructions;
    public EtapePlanVaccination() { }
    public Long getId() { return id; } public void setId(Long value) { id = value; }
    public PlanVaccination getPlan() { return plan; } public void setPlan(PlanVaccination value) { plan = value; }
    public Vaccin getVaccin() { return vaccin; } public void setVaccin(Vaccin value) { vaccin = value; }
    public Integer getOrdreEtape() { return ordreEtape; } public void setOrdreEtape(Integer value) { ordreEtape = value; }
    public Integer getAgeCibleJours() { return ageCibleJours; } public void setAgeCibleJours(Integer value) { ageCibleJours = value; }
    public Integer getToleranceJours() { return toleranceJours; } public void setToleranceJours(Integer value) { toleranceJours = value; }
    public BigDecimal getDoseMl() { return doseMl; } public void setDoseMl(BigDecimal value) { doseMl = value; }
    public VoieAdministration getVoieAdministration() { return voieAdministration; } public void setVoieAdministration(VoieAdministration value) { voieAdministration = value; }
    public String getInstructions() { return instructions; } public void setInstructions(String value) { instructions = value; }
}
