package com.reseau_partage.core.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
@Table(name = "vaccinations_individuelles")
public class VaccinationIndividuelle {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "animal_id", nullable = false) private Long animalId;
    @Column(name = "animal_code", length = 30) private String animalCode;
    @Enumerated(EnumType.STRING) private Espece espece;
    @Column(name = "ferme_id", nullable = false) private Long fermeId;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "vaccin_id", nullable = false) private Vaccin vaccin;
    @Column(name = "numero_lot_vaccin", length = 50) private String numeroLotVaccin;
    @Column(name = "date_expiration_lot") private LocalDate dateExpirationLot;
    @Enumerated(EnumType.STRING) @Column(name = "type_vaccination") private TypeVaccination typeVaccination;
    @Column(name = "numero_dose_dans_protocole") private Integer numeroDoseDansProtocole;
    @Column(name = "date_vaccination", nullable = false) private LocalDate dateVaccination;
    @Column(name = "age_animal_jours_au_moment") private Integer ageAnimalJoursAuMoment;
    @Enumerated(EnumType.STRING) @Column(name = "voie_administration") private VoieAdministration voieAdministration;
    @Column(name = "dose_ml", precision = 6, scale = 2) private BigDecimal doseMl;
    @Column(name = "date_prochain_rappel") private LocalDate dateProchaineRappel;
    @Column(name = "date_fin_delai_attente") private LocalDate dateFinDelaiAttente;
    @Column(name = "reaction_observee", length = 200) private String reactionObservee;
    @Column(name = "veterinaire_nom", length = 100) private String veterinaireNom;
    @Column(name = "operateur_nom", length = 100) private String operateurNom;
    @Column(columnDefinition = "TEXT") private String notes;
    @Column(name = "date_creation", updatable = false) private LocalDateTime dateCreation;
    public VaccinationIndividuelle() { }
    @PrePersist protected void onCreate() { dateCreation = LocalDateTime.now(); }
    public Long getId() { return id; } public void setId(Long v) { id = v; }
    public Long getAnimalId() { return animalId; } public void setAnimalId(Long v) { animalId = v; }
    public String getAnimalCode() { return animalCode; } public void setAnimalCode(String v) { animalCode = v; }
    public Espece getEspece() { return espece; } public void setEspece(Espece v) { espece = v; }
    public Long getFermeId() { return fermeId; } public void setFermeId(Long v) { fermeId = v; }
    public Vaccin getVaccin() { return vaccin; } public void setVaccin(Vaccin v) { vaccin = v; }
    public String getNumeroLotVaccin() { return numeroLotVaccin; } public void setNumeroLotVaccin(String v) { numeroLotVaccin = v; }
    public LocalDate getDateExpirationLot() { return dateExpirationLot; } public void setDateExpirationLot(LocalDate v) { dateExpirationLot = v; }
    public TypeVaccination getTypeVaccination() { return typeVaccination; } public void setTypeVaccination(TypeVaccination v) { typeVaccination = v; }
    public Integer getNumeroDoseDansProtocole() { return numeroDoseDansProtocole; } public void setNumeroDoseDansProtocole(Integer v) { numeroDoseDansProtocole = v; }
    public LocalDate getDateVaccination() { return dateVaccination; } public void setDateVaccination(LocalDate v) { dateVaccination = v; }
    public Integer getAgeAnimalJoursAuMoment() { return ageAnimalJoursAuMoment; } public void setAgeAnimalJoursAuMoment(Integer v) { ageAnimalJoursAuMoment = v; }
    public VoieAdministration getVoieAdministration() { return voieAdministration; } public void setVoieAdministration(VoieAdministration v) { voieAdministration = v; }
    public BigDecimal getDoseMl() { return doseMl; } public void setDoseMl(BigDecimal v) { doseMl = v; }
    public LocalDate getDateProchaineRappel() { return dateProchaineRappel; } public void setDateProchaineRappel(LocalDate v) { dateProchaineRappel = v; }
    public LocalDate getDateFinDelaiAttente() { return dateFinDelaiAttente; } public void setDateFinDelaiAttente(LocalDate v) { dateFinDelaiAttente = v; }
    public String getReactionObservee() { return reactionObservee; } public void setReactionObservee(String v) { reactionObservee = v; }
    public String getVeterinaireNom() { return veterinaireNom; } public void setVeterinaireNom(String v) { veterinaireNom = v; }
    public String getOperateurNom() { return operateurNom; } public void setOperateurNom(String v) { operateurNom = v; }
    public String getNotes() { return notes; } public void setNotes(String v) { notes = v; }
    public LocalDateTime getDateCreation() { return dateCreation; }
}
