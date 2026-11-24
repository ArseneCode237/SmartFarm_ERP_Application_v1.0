package com.reseau_partage.core.entities;

public enum Categorie {
    // ── Porcins ───────────────────────────────────────────────────────────────
    TRUIE,
    VERRAT,
    PORCELET,
    COCHONNET,

    // ── Volailles (Poulet, Dinde, etc.) ───────────────────────────────────────
    POUSSIN,
    POULET_CHAIR,
    PONDEUSE,
    CHAIR,
    MIXTE,

    // ── Commun élevage ────────────────────────────────────────────────────────
    ENGRAISSEMENT,
    REPRODUCTEUR,

    // ── Bovins (rôle / destination dans l'élevage) ─────────────────────────────
    VACHE_LAITIERE,
    VACHE_ALLAITANTE,
    TAUREAU_REPRODUCTEUR,
    VEAU,
    BROUTARD,
    GENISSE,
    TAURILLON,
    BOVIN_CHARCUTIER,
    /** Bœuf de labour ou de transport (traction animale). */
    TRACTION_TRAVAIL,
    /** Animal âgé ou improductif retiré de la reproduction, destiné à la vente. */
    REFORME,

    // ── Aquaculture / Poissons ────────────────────────────────────────────────
    ALEVIN,
    JUVENILE,
    ADULTE
}
