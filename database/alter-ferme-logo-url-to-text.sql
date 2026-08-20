-- =============================================================================
-- Script de migration : Agrandissement de la colonne fermes.logo_url (TEXT)
-- Auteur : SmartFarm ERP
-- But    : Permettre de stocker des logos au format base64 (data:image/...)
--          qui dépassent largement la taille par défaut VARCHAR(255).
-- =============================================================================

ALTER TABLE IF EXISTS fermes
    ALTER COLUMN logo_url TYPE TEXT;

-- Facultatif : commentaire de colonne
COMMENT ON COLUMN fermes.logo_url IS 'Logo de la ferme. Peut être une URL classique ou un data URI base64 (JPEG/PNG/GIF/WEBP/SVG).';
