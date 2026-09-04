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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "plans_vaccination")
public class PlanVaccination {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 150) private String nom;
    @Column(name = "ferme_id", nullable = false) private Long fermeId;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private Espece espece;
    @Enumerated(EnumType.STRING) @Column(name = "type_production") private TypeProduction typeProduction;
    @Column(nullable = false) private Boolean actif = true;
    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true) private List<EtapePlanVaccination> etapes = new ArrayList<>();
    @Column(name = "date_creation", updatable = false) private LocalDateTime dateCreation;
    public PlanVaccination() { }
    @PrePersist protected void onCreate() { dateCreation = LocalDateTime.now(); }
    public Long getId() { return id; } public void setId(Long value) { id = value; }
    public String getNom() { return nom; } public void setNom(String value) { nom = value; }
    public Long getFermeId() { return fermeId; } public void setFermeId(Long value) { fermeId = value; }
    public Espece getEspece() { return espece; } public void setEspece(Espece value) { espece = value; }
    public TypeProduction getTypeProduction() { return typeProduction; } public void setTypeProduction(TypeProduction value) { typeProduction = value; }
    public Boolean getActif() { return actif; } public void setActif(Boolean value) { actif = value; }
    public List<EtapePlanVaccination> getEtapes() { return etapes; } public void setEtapes(List<EtapePlanVaccination> value) { etapes = value; }
    public LocalDateTime getDateCreation() { return dateCreation; }
}
