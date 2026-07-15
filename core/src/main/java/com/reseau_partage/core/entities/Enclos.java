package com.reseau_partage.core.entities;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("ENCLOS")
public class Enclos extends Structure {
    private Integer capaciteMaxAnimaux;
    @Column(length = 50)
    private String typeCloture;
    private Boolean accesEau;
    @Column(length = 200)
    private String especesCompatibles;

    public Integer getCapaciteMaxAnimaux() {
        return capaciteMaxAnimaux;
    }

    public void setCapaciteMaxAnimaux(Integer v) {
        capaciteMaxAnimaux = v;
    }

    public String getTypeCloture() {
        return typeCloture;
    }

    public void setTypeCloture(String v) {
        typeCloture = v;
    }

    public Boolean getAccesEau() {
        return accesEau;
    }

    public void setAccesEau(Boolean v) {
        accesEau = v;
    }

    public String getEspecesCompatibles() {
        return especesCompatibles;
    }

    public void setEspecesCompatibles(String v) {
        especesCompatibles = v;
    }
}
