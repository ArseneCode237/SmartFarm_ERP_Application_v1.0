-- Script de migration : Mise à jour de la contrainte CHECK sur la colonne categorie de la table bandes
-- Ajout des nouvelles catégories pour les volailles (POULET, DINDE, CANARD, PINTADE, PIGEON)
-- Date : 2026-09-01
-- Exécutez ce script dans votre base PostgreSQL (smartfarm_db / ferme_intelligente)

-- 1. Supprimer l'ancienne contrainte CHECK si elle existe
ALTER TABLE bandes DROP CONSTRAINT IF EXISTS bandes_categorie_check;

-- 2. Ajouter la nouvelle contrainte CHECK avec TOUTES les valeurs de l'enum Categorie
ALTER TABLE bandes ADD CONSTRAINT bandes_categorie_check
CHECK (categorie IN (
    -- Porcins
    'TRUIE',
    'VERRAT',
    'PORCELET',
    'COCHONNET',
    -- Volailles (Poulet, Dinde, Canard, Pintade, Pigeon)
    'POUSSIN',
    'POULET_CHAIR',
    'PONDEUSE',
    'CHAIR',
    'MIXTE',
    -- Commun élevage
    'ENGRAISSEMENT',
    'REPRODUCTEUR',
    -- Aquaculture / Poissons
    'ALEVIN',
    'JUVENILE',
    'ADULTE'
));

-- 3. (Optionnel) Vérification : lister les bandes existantes avec une categorie NULL pour audit
-- SELECT id, code_bande, nom, espece, categorie FROM bandes WHERE categorie IS NULL;
