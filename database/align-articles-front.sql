-- Alignement du module Stocks avec le contrat Article du front-end.
-- A executer une seule fois sur les bases deja initialisees avant le deploiement.
ALTER TABLE articles ALTER COLUMN entrepot_id DROP NOT NULL;

ALTER TABLE articles ADD COLUMN IF NOT EXISTS emplacement_stockage VARCHAR(200);
ALTER TABLE articles ADD COLUMN IF NOT EXISTS numero_lot VARCHAR(100);
ALTER TABLE articles ADD COLUMN IF NOT EXISTS notes_internes TEXT;
ALTER TABLE articles ADD COLUMN IF NOT EXISTS est_perissable BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE articles ADD COLUMN IF NOT EXISTS est_suivi_lot BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE articles ADD COLUMN IF NOT EXISTS temperature_conservation VARCHAR(100);
ALTER TABLE articles ADD COLUMN IF NOT EXISTS date_entree DATE;
