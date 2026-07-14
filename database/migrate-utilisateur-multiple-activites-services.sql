-- Permet à un utilisateur de sélectionner plusieurs activités et plusieurs services.
-- À exécuter une seule fois sur PostgreSQL si Hibernate n'est pas autorisé à modifier le schéma.
-- Les valeurs existantes sont conservées dans les nouvelles tables.

CREATE TABLE IF NOT EXISTS utilisateur_type_activites (
    utilisateur_id BIGINT NOT NULL REFERENCES utilisateur(id) ON DELETE CASCADE,
    position INTEGER NOT NULL,
    type_activite VARCHAR(255) NOT NULL,
    PRIMARY KEY (utilisateur_id, position)
);

CREATE TABLE IF NOT EXISTS utilisateur_type_services (
    utilisateur_id BIGINT NOT NULL REFERENCES utilisateur(id) ON DELETE CASCADE,
    position INTEGER NOT NULL,
    type_service VARCHAR(255) NOT NULL,
    PRIMARY KEY (utilisateur_id, position)
);

INSERT INTO utilisateur_type_activites (utilisateur_id, position, type_activite)
SELECT id, 0, lower(btrim(type_activite))
FROM utilisateur
WHERE type_activite IS NOT NULL AND btrim(type_activite) <> ''
ON CONFLICT (utilisateur_id, position) DO NOTHING;

INSERT INTO utilisateur_type_services (utilisateur_id, position, type_service)
SELECT id, 0, lower(btrim(type_service))
FROM utilisateur
WHERE type_service IS NOT NULL AND btrim(type_service) <> ''
ON CONFLICT (utilisateur_id, position) DO NOTHING;
