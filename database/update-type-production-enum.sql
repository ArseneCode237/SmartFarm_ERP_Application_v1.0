-- Script de migration pour mettre à jour l'enum TypeProduction et la contrainte CHECK sur la table bandes
-- Exécutez ce script dans votre base de données PostgreSQL

-- Supprimer la contrainte CHECK existante
ALTER TABLE bandes DROP CONSTRAINT IF EXISTS bandes_type_production_check;

-- Ajouter la nouvelle contrainte CHECK avec toutes les valeurs de l'enum TypeProduction
ALTER TABLE bandes ADD CONSTRAINT bandes_type_production_check 
CHECK (type_production IN (
    'CHAIR',
    'ENGRAISSEMENT',
    'REPRODUCTION',
    'NAISSAGE',
    'NAISSAGE_ENGRAISSEMENT',
    'PONTE',
    'LAIT',
    'VIANDE',
    'AQUACULTURE',
    'MIXTE'
));

-- Mettre à jour aussi la contrainte pour l'enum Provenance si nécessaire
ALTER TABLE bandes DROP CONSTRAINT IF EXISTS bandes_provenance_check;
ALTER TABLE bandes ADD CONSTRAINT bandes_provenance_check 
CHECK (provenance IN (
    'INTERNE',
    'EXTERNE',
    'NAISSANCE_INTERNE',
    'ACHAT_EXTERNE',
    'DON'
));

-- Mettre à jour la contrainte pour l'enum Categorie si nécessaire
ALTER TABLE bandes DROP CONSTRAINT IF EXISTS bandes_categorie_check;
ALTER TABLE bandes ADD CONSTRAINT bandes_categorie_check 
CHECK (categorie IN (
    'TRUIE',
    'VERRAT',
    'PORCELET',
    'COCHONNET',
    'ENGRAISSEMENT',
    'REPRODUCTEUR'
));
