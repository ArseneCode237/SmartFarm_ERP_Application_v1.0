-- Schema du référentiel vaccinal et des actes de santé.
CREATE TABLE IF NOT EXISTS vaccins (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(150) NOT NULL,
    fabricant VARCHAR(100),
    numero_amm VARCHAR(50),
    type_vaccin VARCHAR(30),
    maladies_ciblees VARCHAR(300),
    voie_administration VARCHAR(30),
    dose_ml NUMERIC(6,2),
    age_premiere_dose_jours INTEGER,
    intervalle_rappel_jours INTEGER,
    nombre_doses_protocole INTEGER,
    delai_attente_abattage_jours INTEGER,
    temperature_conservation_min INTEGER,
    temperature_conservation_max INTEGER,
    duree_validite_apres_ouverture_heures INTEGER,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    notes TEXT,
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS vaccin_especes (
    vaccin_id BIGINT NOT NULL REFERENCES vaccins(id) ON DELETE CASCADE,
    espece VARCHAR(30) NOT NULL,
    PRIMARY KEY (vaccin_id, espece)
);

CREATE TABLE IF NOT EXISTS plans_vaccination (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(150) NOT NULL,
    ferme_id BIGINT NOT NULL,
    espece VARCHAR(30) NOT NULL,
    type_production VARCHAR(40),
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS etapes_plan_vaccination (
    id BIGSERIAL PRIMARY KEY,
    plan_id BIGINT NOT NULL REFERENCES plans_vaccination(id) ON DELETE CASCADE,
    vaccin_id BIGINT NOT NULL REFERENCES vaccins(id),
    ordre_etape INTEGER NOT NULL,
    age_cible_jours INTEGER NOT NULL,
    tolerance_jours INTEGER DEFAULT 3,
    dose_ml NUMERIC(6,2),
    voie_administration VARCHAR(30),
    instructions VARCHAR(200)
);

CREATE TABLE IF NOT EXISTS vaccinations_bandes (
    id BIGSERIAL PRIMARY KEY,
    bande_id BIGINT NOT NULL,
    bande_nom VARCHAR(100),
    espece VARCHAR(30),
    ferme_id BIGINT NOT NULL,
    vaccin_id BIGINT NOT NULL REFERENCES vaccins(id),
    numero_lot_vaccin VARCHAR(50),
    date_expiration_lot DATE,
    type_vaccination VARCHAR(30),
    numero_dose_dans_protocole INTEGER,
    date_vaccination DATE NOT NULL,
    age_bande_jours_au_moment INTEGER,
    voie_administration VARCHAR(30),
    dose_ml_par_tete NUMERIC(6,2),
    nb_animaux_vaccines INTEGER NOT NULL,
    dose_totale_ml NUMERIC(10,2),
    statut VARCHAR(30) NOT NULL DEFAULT 'EFFECTUEE',
    date_prochain_rappel DATE,
    plan_vaccination_id BIGINT,
    etape_plan_id BIGINT,
    date_fin_delai_attente DATE,
    veterinaire_nom VARCHAR(100),
    operateur_nom VARCHAR(100),
    notes TEXT,
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS vaccinations_individuelles (
    id BIGSERIAL PRIMARY KEY,
    animal_id BIGINT NOT NULL,
    animal_code VARCHAR(30),
    espece VARCHAR(30),
    ferme_id BIGINT NOT NULL,
    vaccin_id BIGINT NOT NULL REFERENCES vaccins(id),
    numero_lot_vaccin VARCHAR(50),
    date_expiration_lot DATE,
    type_vaccination VARCHAR(30),
    numero_dose_dans_protocole INTEGER,
    date_vaccination DATE NOT NULL,
    age_animal_jours_au_moment INTEGER,
    voie_administration VARCHAR(30),
    dose_ml NUMERIC(6,2),
    date_prochain_rappel DATE,
    date_fin_delai_attente DATE,
    reaction_observee VARCHAR(200),
    veterinaire_nom VARCHAR(100),
    operateur_nom VARCHAR(100),
    notes TEXT,
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_vacc_bande_ferme_rappel ON vaccinations_bandes(ferme_id, date_prochain_rappel);
CREATE INDEX IF NOT EXISTS idx_vacc_individu_ferme_rappel ON vaccinations_individuelles(ferme_id, date_prochain_rappel);
CREATE INDEX IF NOT EXISTS idx_vacc_bande_delai ON vaccinations_bandes(bande_id, date_fin_delai_attente);
