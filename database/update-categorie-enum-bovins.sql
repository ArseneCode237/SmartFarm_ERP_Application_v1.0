-- Script de migration : Mise à jour de la contrainte CHECK sur la colonne categorie de la table bandes
-- Ajout des catégories/rôles bovins (vache laitière, taureau reproducteur, génisse, veau, broutard,
-- taurillon, bovin charcutier, traction/travail, réforme) au référentiel de la fiche Cameroun.
-- Date : 2026-11-24
-- Exécutez ce script dans votre base PostgreSQL (ferme_intelligente)

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
    -- Bovins (rôle / destination dans l'élevage)
    'VACHE_LAITIERE',
    'VACHE_ALLAITANTE',
    'TAUREAU_REPRODUCTEUR',
    'VEAU',
    'BROUTARD',
    'GENISSE',
    'TAURILLON',
    'BOVIN_CHARCUTIER',
    'TRACTION_TRAVAIL',
    'REFORME',
    -- Aquaculture / Poissons
    'ALEVIN',
    'JUVENILE',
    'ADULTE'
));
