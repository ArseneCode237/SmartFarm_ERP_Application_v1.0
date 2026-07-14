-- A executer une seule fois sur une base deja creee.
-- Le script s'arrete si des doublons existent deja: corrigez-les avant de le relancer.

CREATE UNIQUE INDEX IF NOT EXISTS ux_utilisateur_telephone
    ON utilisateur (telephone)
    WHERE telephone IS NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS ux_utilisateur_structure_nom_normalized
    ON utilisateur (lower(btrim(structure_nom)))
    WHERE structure_nom IS NOT NULL AND btrim(structure_nom) <> '';
