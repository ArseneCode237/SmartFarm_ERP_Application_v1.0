package com.reseau_partage.core.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity @Table(name="structures") @Inheritance(strategy=InheritanceType.SINGLE_TABLE) @DiscriminatorColumn(name="type_structure")
public abstract class Structure {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="site_id", nullable=false) private Site site;
    @Column(nullable=false) private String nom; private String description;
    @Column(name="superficie_m2", precision=10, scale=2) private BigDecimal superficieM2;
    @Column(precision=10,scale=7) private BigDecimal latitude; @Column(precision=10,scale=7) private BigDecimal longitude;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private StatutStructure statut;
    @Column(name="date_creation", nullable=false, updatable=false) private LocalDateTime dateCreation;
    @Column(name="date_debut_vide") private LocalDateTime dateDebutVide;
    @PrePersist void prePersist(){if(dateCreation==null)dateCreation=LocalDateTime.now();if(statut==null)statut=StatutStructure.ACTIF;}
    public Long getId(){return id;} public Site getSite(){return site;} public void setSite(Site v){site=v;} public String getNom(){return nom;} public void setNom(String v){nom=v;} public String getDescription(){return description;} public void setDescription(String v){description=v;} public BigDecimal getSuperficieM2(){return superficieM2;} public void setSuperficieM2(BigDecimal v){superficieM2=v;} public BigDecimal getLatitude(){return latitude;} public void setLatitude(BigDecimal v){latitude=v;} public BigDecimal getLongitude(){return longitude;} public void setLongitude(BigDecimal v){longitude=v;} public StatutStructure getStatut(){return statut;} public void setStatut(StatutStructure v){statut=v;} public LocalDateTime getDateCreation(){return dateCreation;} public LocalDateTime getDateDebutVide(){return dateDebutVide;} public void setDateDebutVide(LocalDateTime v){dateDebutVide=v;}
}
