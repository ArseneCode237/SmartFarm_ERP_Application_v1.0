package com.reseau_partage.core.entities.enumtypes;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum UniteMesure {
    KG, TONNE, LITRE, DOSE,
    UNITE, COMPRIME, SACHET, BOITE, SAC

    ;

    @JsonCreator
    public static UniteMesure from(String value) {
        return value == null ? null : valueOf(value.trim().toUpperCase());
    }
}
