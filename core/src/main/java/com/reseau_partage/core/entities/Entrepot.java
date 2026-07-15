package com.reseau_partage.core.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@DiscriminatorValue("ENTREPOT")
public class Entrepot extends Structure {
    @Column(precision = 10, scale = 2)
    private BigDecimal capaciteTonnes;
    private Boolean temperatureControlee;
    @Column(precision = 4, scale = 1)
    private BigDecimal temperatureMinCelsius;
    @Column(precision = 4, scale = 1)
    private BigDecimal temperatureMaxCelsius;

    public BigDecimal getCapaciteTonnes() {
        return capaciteTonnes;
    }

    public void setCapaciteTonnes(BigDecimal v) {
        capaciteTonnes = v;
    }

    public Boolean getTemperatureControlee() {
        return temperatureControlee;
    }

    public void setTemperatureControlee(Boolean v) {
        temperatureControlee = v;
    }

    public BigDecimal getTemperatureMinCelsius() {
        return temperatureMinCelsius;
    }

    public void setTemperatureMinCelsius(BigDecimal v) {
        temperatureMinCelsius = v;
    }

    public BigDecimal getTemperatureMaxCelsius() {
        return temperatureMaxCelsius;
    }

    public void setTemperatureMaxCelsius(BigDecimal v) {
        temperatureMaxCelsius = v;
    }
}
