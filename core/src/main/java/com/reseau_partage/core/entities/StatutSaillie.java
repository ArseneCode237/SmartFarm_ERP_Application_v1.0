package com.reseau_partage.core.entities;

public enum StatutSaillie {
    /** Saillie enregistrée, écho de confirmation pas encore effectué. */
    EN_ATTENTE,
    /** Gestation confirmée par échographie à J+28. */
    CONFIRMEE,
    /** Écho négatif — retour en chaleur attendu. */
    ECHEC,
    /** Gestation interrompue en cours de route. */
    AVORTEMENT
}
