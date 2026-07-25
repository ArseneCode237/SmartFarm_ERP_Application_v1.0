package com.reseau_partage.core.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "courbes_croissance_reference")
public class CourbeCroissanceReference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "config_espece_id")
    private ConfigEspece configEspece;

    @Column(length = 100)
    private String race;

    @Column(name = "age_jours", nullable = false)
    private Integer ageJours;

    @Column(name = "poids_cible_kg", precision = 8, scale = 3)
    private BigDecimal poidsCibleKg;

    @Column(name = "poids_mini_kg", precision = 8, scale = 3)
    private BigDecimal poidsMiniKg;

    @Column(name = "poids_maxi_kg", precision = 8, scale = 3)
    private BigDecimal poidsMaxiKg;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ConfigEspece getConfigEspece() { return configEspece; }
    public void setConfigEspece(ConfigEspece configEspece) { this.configEspece = configEspece; }
    public String getRace() { return race; }
    public void setRace(String race) { this.race = race; }
    public Integer getAgeJours() { return ageJours; }
    public void setAgeJours(Integer ageJours) { this.ageJours = ageJours; }
    public BigDecimal getPoidsCibleKg() { return poidsCibleKg; }
    public void setPoidsCibleKg(BigDecimal poidsCibleKg) { this.poidsCibleKg = poidsCibleKg; }
    public BigDecimal getPoidsMiniKg() { return poidsMiniKg; }
    public void setPoidsMiniKg(BigDecimal poidsMiniKg) { this.poidsMiniKg = poidsMiniKg; }
    public BigDecimal getPoidsMaxiKg() { return poidsMaxiKg; }
    public void setPoidsMaxiKg(BigDecimal poidsMaxiKg) { this.poidsMaxiKg = poidsMaxiKg; }
}
