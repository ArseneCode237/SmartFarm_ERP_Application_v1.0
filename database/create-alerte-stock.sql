-- Table des alertes de stock calculées par le backend.
-- Colonne résumée côté article pour éviter des JOIN coûteux à la lecture.
CREATE TABLE IF NOT EXISTS alerte_stock (
    id BIGSERIAL PRIMARY KEY,
    ferme_id BIGINT NOT NULL,
    article_id BIGINT NOT NULL REFERENCES articles(id) ON DELETE CASCADE,
    article_code VARCHAR(30) NOT NULL,
    article_designation VARCHAR(200) NOT NULL,
    article_categorie VARCHAR(20) NOT NULL,
    article_unite_mesure VARCHAR(20) NOT NULL,
    article_emplacement VARCHAR(200),
    article_numero_lot VARCHAR(100),
    article_temperature_conservation VARCHAR(100),
    type_alerte VARCHAR(30) NOT NULL,
    priorite VARCHAR(20) NOT NULL,
    titre VARCHAR(200) NOT NULL,
    description TEXT,
    stock_actuel NUMERIC(12,3),
    seuil_alerte_min NUMERIC(12,3),
    seuil_alerte_critique NUMERIC(12,3),
    date_peremption DATE,
    jours_avant_peremption INTEGER,
    resolue BOOLEAN NOT NULL DEFAULT FALSE,
    date_resolution TIMESTAMP,
    commentaire_resolution TEXT,
    cree_par BIGINT,
    resolu_par BIGINT,
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_alerte_stock_ferme ON alerte_stock(ferme_id);
CREATE INDEX IF NOT EXISTS idx_alerte_stock_article ON alerte_stock(article_id);
CREATE INDEX IF NOT EXISTS idx_alerte_stock_resolue ON alerte_stock(resolue);
CREATE INDEX IF NOT EXISTS idx_alerte_stock_type ON alerte_stock(type_alerte);
CREATE INDEX IF NOT EXISTS idx_alerte_stock_ferme_resolue ON alerte_stock(ferme_id, resolue);
CREATE INDEX IF NOT EXISTS idx_alerte_stock_ferme_type ON alerte_stock(ferme_id, type_alerte);
