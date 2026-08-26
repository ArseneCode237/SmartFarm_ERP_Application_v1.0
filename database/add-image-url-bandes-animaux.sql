-- =============================================================================
-- Script de migration : Ajout du champ image (bandes.image_url, animaux.image_url)
-- Auteur : SmartFarm ERP
-- But    : Permettre l'enregistrement d'une image (URL classique ou data URI
--          base64 JPEG/PNG/GIF/WEBP) pour la création des bandes et le
--          suivi individuel des animaux.
-- =============================================================================

ALTER TABLE bandes ADD COLUMN IF NOT EXISTS image_url TEXT;

ALTER TABLE animaux ADD COLUMN IF NOT EXISTS image_url TEXT;

COMMENT ON COLUMN bandes.image_url IS 'Image de la bande. URL classique ou data URI base64 (JPEG/PNG/GIF/WEBP).';
COMMENT ON COLUMN animaux.image_url IS 'Image de l''animal. URL classique ou data URI base64 (JPEG/PNG/GIF/WEBP).';
