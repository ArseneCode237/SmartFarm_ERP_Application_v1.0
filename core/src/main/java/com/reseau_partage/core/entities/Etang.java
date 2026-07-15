package com.reseau_partage.core.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@DiscriminatorValue("ETANG")
public class Etang extends Structure {
    @Column(precision = 10, scale = 2)
    private BigDecimal volumeM3;
    @Column(precision = 5, scale = 2)
    private BigDecimal profondeurM;
    @Column(length = 50)
    private String systemeAeration;
    @Column(precision = 4, scale = 1)
    private BigDecimal temperatureCibleCelsius;
    @Column(precision = 3, scale = 1)
    private BigDecimal phCible;

    public BigDecimal getVolumeM3() {
        return volumeM3;
    }

    public void setVolumeM3(BigDecimal v) {
        volumeM3 = v;
    }

    public BigDecimal getProfondeurM() {
        return profondeurM;
    }

    public void setProfondeurM(BigDecimal v) {
        profondeurM = v;
    }

    public String getSystemeAeration() {
        return systemeAeration;
    }

    public void setSystemeAeration(String v) {
        systemeAeration = v;
    }

    public BigDecimal getTemperatureCibleCelsius() {
        return temperatureCibleCelsius;
    }

    public void setTemperatureCibleCelsius(BigDecimal v) {
        temperatureCibleCelsius = v;
    }

    public BigDecimal getPhCible() {
        return phCible;
    }

    public void setPhCible(BigDecimal v) {
        phCible = v;
    }
}
