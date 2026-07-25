package com.reseau_partage.core.entities;

public enum StatutReproductifPorcin {
    /** Jeune femelle sélectionnée, pas encore saillée. */
    COCHETTE,
    /** Prête pour la saillie — chaleurs attendues. */
    EN_ATTENTE_SAILLIE,
    /** Accouplée ou inséminée, résultat inconnu. */
    SAILLIE,
    /** Gestation confirmée par échographie à J+28. */
    GESTATION,
    /** J-7 avant terme, transfert en loge maternité. */
    PRE_MISE_BAS,
    /** Parturition en cours. */
    MISE_BAS,
    /** Allaite sa portée. */
    LACTATION,
    /** Porcelets sevrés, truie disponible. */
    SEVRAGE,
    /** Retour en chaleurs post-sevrage (5-7 jours). */
    EN_CHALEUR,
    /** Fin de carrière reproductive — à réformer. */
    REFORME,
    /** Mâle reproducteur. */
    VERRAT
}
