package com.reseau_partage.core.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "controles_laitiers", indexes = {
        @Index(name = "idx_controle_laitier_animal", columnList = "animal_id"),
        @Index(name = "idx_controle_laitier_date", columnList = "date_controle")
}, uniqueConstraints = @UniqueConstraint(name = "uk_controle_laitier_journalier",
        columnNames = {"animal_id", "numero_lactation", "date_controle"}))
public class ControleLaitier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "animal_id", nullable = false)
    private Long animalId;

    @Column(name = "ferme_id", nullable = false)
    private Long fermeId;

    @Column(name = "date_controle", nullable = false)
    private LocalDate dateControle;

    @Column(name = "jours_en_lait_au_moment")
    private Integer joursEnLaitAuMoment;

    @Column(name = "numero_lactation")
    private Integer numeroLactation;

    @Column(name = "lait_matin_kg", precision = 6, scale = 2)
    private BigDecimal laitMatinKg;

    @Column(name = "lait_midi_kg", precision = 6, scale = 2)
    private BigDecimal laitMidiKg;

    @Column(name = "lait_soir_kg", precision = 6, scale = 2)
    private BigDecimal laitSoirKg;

    @Column(name = "lait_total_jour_kg", precision = 6, scale = 2, nullable = false)
    private BigDecimal laitTotalJourKg;

    @Column(name = "taux_butyreux_pct", precision = 5, scale = 2)
    private BigDecimal tauxButyreuxPct;

    @Column(name = "taux_proteique_pct", precision = 5, scale = 2)
    private BigDecimal tauxProteiquePct;

    @Column(name = "cellules_somatiques_milliers")
    private Integer cellulesSomatiquesMilliers;

    @Column(name = "lait_cible_jour_kg", precision = 6, scale = 2)
    private BigDecimal laitCibleJourKg;

    @Column(name = "ecart_vs_cible_pct", precision = 5, scale = 2)
    private BigDecimal ecartVsCiblePct;

    @Column(name = "lait_cumul_lactation_kg", precision = 10, scale = 2)
    private BigDecimal laitCumulLactationKg;

    @Column(name = "alerte_generee", nullable = false)
    private Boolean alerteGeneree = false;

    @Column(name = "type_alerte", length = 50)
    private String typeAlerte;

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
    public Long getAnimalId() { return animalId; }
    public void setAnimalId(Long value) { animalId = value; }
    public Long getFermeId() { return fermeId; }
    public void setFermeId(Long value) { fermeId = value; }
    public LocalDate getDateControle() { return dateControle; }
    public void setDateControle(LocalDate value) { dateControle = value; }
    public Integer getJoursEnLaitAuMoment() { return joursEnLaitAuMoment; }
    public void setJoursEnLaitAuMoment(Integer value) { joursEnLaitAuMoment = value; }
    public Integer getNumeroLactation() { return numeroLactation; }
    public void setNumeroLactation(Integer value) { numeroLactation = value; }
    public BigDecimal getLaitMatinKg() { return laitMatinKg; }
    public void setLaitMatinKg(BigDecimal value) { laitMatinKg = value; }
    public BigDecimal getLaitMidiKg() { return laitMidiKg; }
    public void setLaitMidiKg(BigDecimal value) { laitMidiKg = value; }
    public BigDecimal getLaitSoirKg() { return laitSoirKg; }
    public void setLaitSoirKg(BigDecimal value) { laitSoirKg = value; }
    public BigDecimal getLaitTotalJourKg() { return laitTotalJourKg; }
    public void setLaitTotalJourKg(BigDecimal value) { laitTotalJourKg = value; }
    public BigDecimal getTauxButyreuxPct() { return tauxButyreuxPct; }
    public void setTauxButyreuxPct(BigDecimal value) { tauxButyreuxPct = value; }
    public BigDecimal getTauxProteiquePct() { return tauxProteiquePct; }
    public void setTauxProteiquePct(BigDecimal value) { tauxProteiquePct = value; }
    public Integer getCellulesSomatiquesMilliers() { return cellulesSomatiquesMilliers; }
    public void setCellulesSomatiquesMilliers(Integer value) { cellulesSomatiquesMilliers = value; }
    public BigDecimal getLaitCibleJourKg() { return laitCibleJourKg; }
    public void setLaitCibleJourKg(BigDecimal value) { laitCibleJourKg = value; }
    public BigDecimal getEcartVsCiblePct() { return ecartVsCiblePct; }
    public void setEcartVsCiblePct(BigDecimal value) { ecartVsCiblePct = value; }
    public BigDecimal getLaitCumulLactationKg() { return laitCumulLactationKg; }
    public void setLaitCumulLactationKg(BigDecimal value) { laitCumulLactationKg = value; }
    public Boolean getAlerteGeneree() { return alerteGeneree; }
    public void setAlerteGeneree(Boolean value) { alerteGeneree = value; }
    public String getTypeAlerte() { return typeAlerte; }
    public void setTypeAlerte(String value) { typeAlerte = value; }
    public String getOperateurNom() { return operateurNom; }
    public void setOperateurNom(String value) { operateurNom = value; }
    public String getNotes() { return notes; }
    public void setNotes(String value) { notes = value; }
}
