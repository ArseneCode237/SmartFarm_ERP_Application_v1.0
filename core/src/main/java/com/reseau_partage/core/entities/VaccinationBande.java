package com.reseau_partage.core.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.reseau_partage.core.entities.enumtypes.StatutVaccination;
import com.reseau_partage.core.entities.enumtypes.TypeVaccination;
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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "vaccinations_bandes")
public class VaccinationBande {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "bande_id", nullable = false) private Long bandeId;
    @Column(name = "bande_nom", length = 100) private String bandeNom;
    @Enumerated(EnumType.STRING) private Espece espece;
    @Column(name = "ferme_id", nullable = false) private Long fermeId;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "vaccin_id", nullable = false) private Vaccin vaccin;
    @Column(name = "numero_lot_vaccin", length = 50) private String numeroLotVaccin;
    @Column(name = "date_expiration_lot") private LocalDate dateExpirationLot;
    @Enumerated(EnumType.STRING) @Column(name = "type_vaccination") private TypeVaccination typeVaccination;
    @Column(name = "numero_dose_dans_protocole") private Integer numeroDoseDansProtocole;
    @Column(name = "date_vaccination", nullable = false) private LocalDate dateVaccination;
    @Column(name = "age_bande_jours_au_moment") private Integer ageBandeJoursAuMoment;
    @Enumerated(EnumType.STRING) @Column(name = "voie_administration") private VoieAdministration voieAdministration;
    @Column(name = "dose_ml_par_tete", precision = 6, scale = 2) private BigDecimal doseMlParTete;
    @Column(name = "nb_animaux_vaccines", nullable = false) private Integer nbAnimauxVaccines;
    @Column(name = "dose_totale_ml", precision = 10, scale = 2) private BigDecimal doseTotaleMl;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private StatutVaccination statut = StatutVaccination.EFFECTUEE;
    @Column(name = "date_prochain_rappel") private LocalDate dateProchaineRappel;
    @Column(name = "plan_vaccination_id") private Long planVaccinationId;
    @Column(name = "etape_plan_id") private Long etapePlanId;
    @Column(name = "date_fin_delai_attente") private LocalDate dateFinDelaiAttente;
    @Column(name = "veterinaire_nom", length = 100) private String veterinaireNom;
    @Column(name = "operateur_nom", length = 100) private String operateurNom;
    @Column(columnDefinition = "TEXT") private String notes;
    @Column(name = "date_creation", updatable = false) private LocalDateTime dateCreation;
    public VaccinationBande() { }
    @PrePersist protected void onCreate() { dateCreation = LocalDateTime.now(); }
    public Long getId() { return id; } public void setId(Long v) { id = v; }
    public Long getBandeId() { return bandeId; } public void setBandeId(Long v) { bandeId = v; }
    public String getBandeNom() { return bandeNom; } public void setBandeNom(String v) { bandeNom = v; }
    public Espece getEspece() { return espece; } public void setEspece(Espece v) { espece = v; }
    public Long getFermeId() { return fermeId; } public void setFermeId(Long v) { fermeId = v; }
    public Vaccin getVaccin() { return vaccin; } public void setVaccin(Vaccin v) { vaccin = v; }
    public String getNumeroLotVaccin() { return numeroLotVaccin; } public void setNumeroLotVaccin(String v) { numeroLotVaccin = v; }
    public LocalDate getDateExpirationLot() { return dateExpirationLot; } public void setDateExpirationLot(LocalDate v) { dateExpirationLot = v; }
    public TypeVaccination getTypeVaccination() { return typeVaccination; } public void setTypeVaccination(TypeVaccination v) { typeVaccination = v; }
    public Integer getNumeroDoseDansProtocole() { return numeroDoseDansProtocole; } public void setNumeroDoseDansProtocole(Integer v) { numeroDoseDansProtocole = v; }
    public LocalDate getDateVaccination() { return dateVaccination; } public void setDateVaccination(LocalDate v) { dateVaccination = v; }
    public Integer getAgeBandeJoursAuMoment() { return ageBandeJoursAuMoment; } public void setAgeBandeJoursAuMoment(Integer v) { ageBandeJoursAuMoment = v; }
    public VoieAdministration getVoieAdministration() { return voieAdministration; } public void setVoieAdministration(VoieAdministration v) { voieAdministration = v; }
    public BigDecimal getDoseMlParTete() { return doseMlParTete; } public void setDoseMlParTete(BigDecimal v) { doseMlParTete = v; }
    public Integer getNbAnimauxVaccines() { return nbAnimauxVaccines; } public void setNbAnimauxVaccines(Integer v) { nbAnimauxVaccines = v; }
    public BigDecimal getDoseTotaleMl() { return doseTotaleMl; } public void setDoseTotaleMl(BigDecimal v) { doseTotaleMl = v; }
    public StatutVaccination getStatut() { return statut; } public void setStatut(StatutVaccination v) { statut = v; }
    public LocalDate getDateProchaineRappel() { return dateProchaineRappel; } public void setDateProchaineRappel(LocalDate v) { dateProchaineRappel = v; }
    public Long getPlanVaccinationId() { return planVaccinationId; } public void setPlanVaccinationId(Long v) { planVaccinationId = v; }
    public Long getEtapePlanId() { return etapePlanId; } public void setEtapePlanId(Long v) { etapePlanId = v; }
    public LocalDate getDateFinDelaiAttente() { return dateFinDelaiAttente; } public void setDateFinDelaiAttente(LocalDate v) { dateFinDelaiAttente = v; }
    public String getVeterinaireNom() { return veterinaireNom; } public void setVeterinaireNom(String v) { veterinaireNom = v; }
    public String getOperateurNom() { return operateurNom; } public void setOperateurNom(String v) { operateurNom = v; }
    public String getNotes() { return notes; } public void setNotes(String v) { notes = v; }
    public LocalDateTime getDateCreation() { return dateCreation; }
}
