-- Add unique constraints to animal codes (code_rfid and code_boucle)
-- These constraints ensure that no two animals can have the same code RFID or code boucle

-- Add unique constraint for code_rfid (only if the value is not null)
CREATE UNIQUE INDEX IF NOT EXISTS ux_animal_code_rfid
    ON animaux (code_rfid)
    WHERE code_rfid IS NOT NULL;

-- Add unique constraint for code_boucle (only if the value is not null)
CREATE UNIQUE INDEX IF NOT EXISTS ux_animal_code_boucle
    ON animaux (code_boucle)
    WHERE code_boucle IS NOT NULL;
