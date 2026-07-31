-- Alignement du module Mouvements avec le contrat du front-end.
-- A executer une seule fois sur les bases deja initialisees avant le deploiement.
ALTER TABLE mouvements_stock ADD COLUMN IF NOT EXISTS fournisseur_nom VARCHAR(150);

-- Les anciennes valeurs sont converties vers les cinq types exposes par l'API.
UPDATE mouvements_stock SET type_mouvement = 'AJUSTEMENT' WHERE type_mouvement IN ('AJUSTEMENT_POSITIF', 'AJUSTEMENT_NEGATIF');
UPDATE mouvements_stock SET type_mouvement = 'TRANSFERT' WHERE type_mouvement IN ('TRANSFERT_SORTIE', 'TRANSFERT_ENTREE');

-- MotifMouvement est persiste avec EnumType.STRING. Les donnees creees avant
-- l'alignement du contrat front-end gardent donc les anciens noms Java ; elles
-- doivent etre converties avant que Hibernate ne puisse les relire.
-- La contrainte existante autorise encore uniquement les anciens noms : elle
-- doit etre retiree temporairement pour permettre la conversion.
ALTER TABLE mouvements_stock DROP CONSTRAINT IF EXISTS mouvements_stock_motif_mouvement_check;

UPDATE mouvements_stock AS mouvement
SET motif_mouvement = correspondance.nouveau_motif
FROM (
    VALUES
        ('RETOUR_FOURNISSEUR', 'RETOUR'),
        ('PRODUCTION_INTERNE', 'PRODUCTION'),
        ('UTILISATION_BANDE', 'CONSO_BANDE'),
        ('UTILISATION_INDIVIDU', 'CONSO_BANDE'),
        ('VACCINATION', 'CONSO_BANDE'),
        ('TRAITEMENT', 'CONSO_BANDE'),
        ('VOL', 'PERTE'),
        ('PEREMPTION', 'PERIME'),
        ('DON_SORTIE', 'DON'),
        ('CORRECTION', 'AUTRE'),
        ('TRANSFERT', 'TRANSFERT_ENTREPOT')
) AS correspondance(ancien_motif, nouveau_motif)
WHERE mouvement.motif_mouvement = correspondance.ancien_motif;

ALTER TABLE mouvements_stock
ADD CONSTRAINT mouvements_stock_motif_mouvement_check
CHECK (motif_mouvement IS NULL OR motif_mouvement IN (
    'ACHAT', 'PRODUCTION', 'RETOUR', 'DON', 'CONSO_BANDE', 'VENTE',
    'DISTRIBUTION', 'PERTE', 'TRANSFERT_ENTREPOT', 'INVENTAIRE', 'PERIME', 'AUTRE'
));
