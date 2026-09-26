package com.reseau_partage.core.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.reseau_partage.core.entities.enumtypes.TypeVaccin;
import com.reseau_partage.core.entities.enumtypes.VoieAdministration;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "vaccins")
public class Vaccin {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 150) private String nom;
    @Column(length = 100) private String fabricant;
    @Column(name = "numero_amm", length = 50) private String numeroAmm;
    @Enumerated(EnumType.STRING) @Column(name = "type_vaccin") private TypeVaccin typeVaccin;
    @ElementCollection @CollectionTable(name = "vaccin_especes", joinColumns = @JoinColumn(name = "vaccin_id"))
    @Enumerated(EnumType.STRING) @Column(name = "espece") private List<Espece> especesCompatibles = new ArrayList<>();
    @Column(name = "maladies_ciblees", length = 300) private String maladiesCiblees;
    @Enumerated(EnumType.STRING) @Column(name = "voie_administration") private VoieAdministration voieAdministration;
    @Column(name = "dose_ml", precision = 6, scale = 2) private BigDecimal doseMl;
    @Column(name = "age_premiere_dose_jours") private Integer agePremiereDoseJours;
    @Column(name = "intervalle_rappel_jours") private Integer intervalleRappelJours;
    @Column(name = "nombre_doses_protocole") private Integer nombreDosesProtocole;
    @Column(name = "delai_attente_abattage_jours") private Integer delaiAttenteAbattageJours;
    @Column(name = "temperature_conservation_min") private Integer temperatureConservationMin;
    @Column(name = "temperature_conservation_max") private Integer temperatureConservationMax;
    @Column(name = "duree_validite_apres_ouverture_heures") private Integer dureeValiditeApresOuvertureHeures;
    @Column(nullable = false) private Boolean actif = true;
    @Column(columnDefinition = "TEXT") private String notes;
    @Column(name = "date_creation", updatable = false) private LocalDateTime dateCreation;

    public Vaccin() { }
    @PrePersist protected void onCreate() { dateCreation = LocalDateTime.now(); }
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; } public void setNom(String nom) { this.nom = nom; }
    public String getFabricant() { return fabricant; } public void setFabricant(String fabricant) { this.fabricant = fabricant; }
    public String getNumeroAmm() { return numeroAmm; } public void setNumeroAmm(String value) { numeroAmm = value; }
    public TypeVaccin getTypeVaccin() { return typeVaccin; } public void setTypeVaccin(TypeVaccin value) { typeVaccin = value; }
    public List<Espece> getEspecesCompatibles() { return especesCompatibles; } public void setEspecesCompatibles(List<Espece> value) { especesCompatibles = value; }
    public String getMaladiesCiblees() { return maladiesCiblees; } public void setMaladiesCiblees(String value) { maladiesCiblees = value; }
    public VoieAdministration getVoieAdministration() { return voieAdministration; } public void setVoieAdministration(VoieAdministration value) { voieAdministration = value; }
    public BigDecimal getDoseMl() { return doseMl; } public void setDoseMl(BigDecimal value) { doseMl = value; }
    public Integer getAgePremiereDoseJours() { return agePremiereDoseJours; } public void setAgePremiereDoseJours(Integer value) { agePremiereDoseJours = value; }
    public Integer getIntervalleRappelJours() { return intervalleRappelJours; } public void setIntervalleRappelJours(Integer value) { intervalleRappelJours = value; }
    public Integer getNombreDosesProtocole() { return nombreDosesProtocole; } public void setNombreDosesProtocole(Integer value) { nombreDosesProtocole = value; }
    public Integer getDelaiAttenteAbattageJours() { return delaiAttenteAbattageJours; } public void setDelaiAttenteAbattageJours(Integer value) { delaiAttenteAbattageJours = value; }
    public Integer getTemperatureConservationMin() { return temperatureConservationMin; } public void setTemperatureConservationMin(Integer value) { temperatureConservationMin = value; }
    public Integer getTemperatureConservationMax() { return temperatureConservationMax; } public void setTemperatureConservationMax(Integer value) { temperatureConservationMax = value; }
    public Integer getDureeValiditeApresOuvertureHeures() { return dureeValiditeApresOuvertureHeures; } public void setDureeValiditeApresOuvertureHeures(Integer value) { dureeValiditeApresOuvertureHeures = value; }
    public Boolean getActif() { return actif; } public void setActif(Boolean value) { actif = value; }
    public String getNotes() { return notes; } public void setNotes(String value) { notes = value; }
    public LocalDateTime getDateCreation() { return dateCreation; }
}
