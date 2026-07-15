package com.reseau_partage.core.entities;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("BATIMENT")
public class Batiment extends Structure {
    private Integer capaciteMaxAnimaux;
    @Column(length = 50)
    private String typeVentilation;
    private Integer dureeVideSanitaireJours;
    private Integer nombreRangees;
    @Column(length = 50)
    private String systemeAbreuvement;

    public Integer getCapaciteMaxAnimaux() {
        return capaciteMaxAnimaux;
    }

    public void setCapaciteMaxAnimaux(Integer v) {
        capaciteMaxAnimaux = v;
    }

    public String getTypeVentilation() {
        return typeVentilation;
    }

    public void setTypeVentilation(String v) {
        typeVentilation = v;
    }

    public Integer getDureeVideSanitaireJours() {
        return dureeVideSanitaireJours;
    }

    public void setDureeVideSanitaireJours(Integer v) {
        dureeVideSanitaireJours = v;
    }

    public Integer getNombreRangees() {
        return nombreRangees;
    }

    public void setNombreRangees(Integer v) {
        nombreRangees = v;
    }

    public String getSystemeAbreuvement() {
        return systemeAbreuvement;
    }

    public void setSystemeAbreuvement(String v) {
        systemeAbreuvement = v;
    }
}
