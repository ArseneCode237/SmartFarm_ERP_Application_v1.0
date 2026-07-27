-- Script de création des loges (cases d'un bâtiment)
-- Date: 2026-07-27

-- ────────────────────────────────────────────────────────────────────────
-- 1. Création de la table loges
-- ────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS loges (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE,
    nom VARCHAR(100) NOT NULL,
    description TEXT,
    capacite_max_animaux INTEGER NOT NULL DEFAULT 0,
    superficie_m2 NUMERIC(10, 2),
    batiment_id BIGINT NOT NULL,
    bande_id BIGINT,
    date_creation TIMESTAMP,
    date_modification TIMESTAMP,

    -- Contraintes de clé étrangère
    CONSTRAINT fk_loge_batiment FOREIGN KEY (batiment_id)
        REFERENCES structures(id) ON DELETE CASCADE,
    CONSTRAINT fk_loge_bande FOREIGN KEY (bande_id)
        REFERENCES bandes(id) ON DELETE SET NULL,

    -- Contraintes de valeur
    CONSTRAINT chk_loge_capacite CHECK (capacite_max_animaux >= 0),
    CONSTRAINT chk_loge_superficie CHECK (superficie_m2 IS NULL OR superficie_m2 >= 0)
);

-- Index pour optimiser les recherches
CREATE INDEX IF NOT EXISTS idx_loge_batiment_id ON loges(batiment_id);
CREATE INDEX IF NOT EXISTS idx_loge_bande_id ON loges(bande_id);

-- ────────────────────────────────────────────────────────────────────────
-- 2. Ajout de la colonne loge_id dans la table animaux
-- ────────────────────────────────────────────────────────────────────────
ALTER TABLE animaux ADD COLUMN IF NOT EXISTS loge_id BIGINT;
ALTER TABLE animaux ADD CONSTRAINT IF NOT EXISTS fk_animal_loge
    FOREIGN KEY (loge_id) REFERENCES loges(id) ON DELETE SET NULL;

CREATE INDEX IF NOT EXISTS idx_animal_loge_id ON animaux(loge_id);

-- ────────────────────────────────────────────────────────────────────────
-- FIN DU SCRIPT
-- ────────────────────────────────────────────────────────────────────────
