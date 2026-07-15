package com.reseau_partage.core.entities;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("PARCELLE")
public class Parcelle extends Structure {
    @Column(length = 50)
    private String typeSol;
    @Column(length = 100)
    private String cultureActuelle;
    @Column(length = 50)
    private String systemeIrrigation;
    @Column(columnDefinition = "TEXT")
    private String coordonneesPolygone;

    public String getTypeSol() {
        return typeSol;
    }

    public void setTypeSol(String v) {
        typeSol = v;
    }

    public String getCultureActuelle() {
        return cultureActuelle;
    }

    public void setCultureActuelle(String v) {
        cultureActuelle = v;
    }

    public String getSystemeIrrigation() {
        return systemeIrrigation;
    }

    public void setSystemeIrrigation(String v) {
        systemeIrrigation = v;
    }

    public String getCoordonneesPolygone() {
        return coordonneesPolygone;
    }

    public void setCoordonneesPolygone(String v) {
        coordonneesPolygone = v;
    }
}
