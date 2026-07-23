-- Add new columns to bandes table
ALTER TABLE bandes ADD COLUMN total_declares_morts INTEGER DEFAULT 0;
ALTER TABLE bandes ADD COLUMN total_declares_vendus INTEGER DEFAULT 0;
ALTER TABLE bandes ADD COLUMN total_declares_reformes INTEGER DEFAULT 0;
ALTER TABLE bandes ADD COLUMN revenu_total_ventes NUMERIC(15,2) DEFAULT 0;
ALTER TABLE bandes ADD COLUMN date_derniere_declaration DATE;

-- Create declarations_bande table
CREATE TABLE declarations_bande (
    id BIGSERIAL PRIMARY KEY,
    bande_id BIGINT NOT NULL,
    bande_nom VARCHAR(100),
    espece VARCHAR(30),
    ferme_id BIGINT NOT NULL,
    utilisateur_id BIGINT,
    utilisateur_nom VARCHAR(150),
    type VARCHAR(10) NOT NULL,
    motif VARCHAR(50) NOT NULL,
    date_declaration DATE NOT NULL,
    date_creation TIMESTAMP,
    date_modification TIMESTAMP,
    quantite INTEGER NOT NULL,
    effectif_avant_declaration INTEGER,
    effectif_apres_declaration INTEGER,
    poids_moyen_kg NUMERIC(8,3),
    poids_total_kg NUMERIC(10,3),
    prix_par_kg BOOLEAN DEFAULT FALSE,
    prix_unitaire NUMERIC(15,2),
    montant_total NUMERIC(15,2),
    nom_acheteur VARCHAR(255),
    telephone_acheteur VARCHAR(20),
    localite_acheteur VARCHAR(150),
    observations TEXT,
    source VARCHAR(20) DEFAULT 'MANUEL',
    statut VARCHAR(10) DEFAULT 'ACTIF',
    motif_annulation VARCHAR(200),
    date_annulation TIMESTAMP,
    utilisateur_annulation_id BIGINT
);

CREATE INDEX idx_decl_bande_id ON declarations_bande(bande_id);
CREATE INDEX idx_decl_ferme_id ON declarations_bande(ferme_id);
CREATE INDEX idx_decl_type ON declarations_bande(type);
CREATE INDEX idx_decl_date ON declarations_bande(date_declaration);
CREATE INDEX idx_decl_statut ON declarations_bande(statut);

-- Create declarations_historique table
CREATE TABLE declarations_historique (
    id BIGSERIAL PRIMARY KEY,
    declaration_id BIGINT NOT NULL,
    action VARCHAR(20) NOT NULL,
    anciennes_valeurs JSONB,
    nouvelles_valeurs JSONB,
    utilisateur_id BIGINT,
    utilisateur_nom VARCHAR(150),
    date_action TIMESTAMP NOT NULL,
    ip_adresse VARCHAR(45)
);
