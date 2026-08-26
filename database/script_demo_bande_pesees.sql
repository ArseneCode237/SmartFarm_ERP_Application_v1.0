-- ============================================================================
-- SCRIPT DE DÉMONSTRATION : BANDE + PESÉES + GMQ
-- Base : PostgreSQL  |  smartfarm_db
-- Objectif : créer une bande avec 6 pesées espacées de 15 jours (croissance
-- régulière ~600 g/j) pour visualiser le rendu sur la page Bilan des pesées.
-- Exécution : psql -U postgres -d smartfarm_db -f script_demo_bande_pesees.sql
-- ============================================================================

DO $$
DECLARE
    v_structure_id BIGINT;
    v_site_id      BIGINT;
    v_bande_id     BIGINT;
BEGIN

    -- ── 1. RÉCUPÉRER UNE STRUCTURE ACTIVE DE LA FERME « romualdFerme » ──
    SELECT s.id, s.site_id
      INTO v_structure_id, v_site_id
      FROM structures s
      JOIN sites si ON si.id = s.site_id
     WHERE s.statut = 'ACTIF'
       AND si.ferme_id = (
           SELECT f.id FROM fermes f
            WHERE REPLACE(LOWER(f.nom), ' ', '') = 'romualdferme'
            LIMIT 1)
     ORDER BY s.id
     LIMIT 1;

    -- Repli : première structure active de n'importe quelle ferme
    IF v_structure_id IS NULL THEN
        SELECT s.id, s.site_id
          INTO v_structure_id, v_site_id
          FROM structures s
         WHERE s.statut = 'ACTIF'
           AND s.site_id IS NOT NULL
         ORDER BY s.id
         LIMIT 1;
        IF v_structure_id IS NULL THEN
            RAISE EXCEPTION 'Aucune structure active trouvée. Créez d''abord un site et une structure depuis l''application.';
        END IF;
        RAISE WARNING '⚠️ Ferme « romualdFerme » introuvable ou sans structure : bande rattachée à une autre ferme (structure %).', v_structure_id;
    END IF;

    -- ── 2. CRÉER LA BANDE (entrée il y a 90 jours) ──
    DELETE FROM pesees WHERE bande_id IN (SELECT id FROM bandes WHERE code_bande = 'PO-DEMO-001');
    DELETE FROM bandes WHERE code_bande = 'PO-DEMO-001';

    INSERT INTO bandes (
        code_bande, nom, espece, race, type_production,
        structure_id, site_id,
        provenance, effectif_initial, effectif_actuel,
        effectif_morts, effectif_vendus, effectif_reformes,
        total_declares_morts, total_declares_vendus, total_declares_reformes,
        revenu_total_ventes,
        date_entree, statut,
        poids_moyen_entree_kg, ration_journaliere_kg,
        description, date_creation
    ) VALUES (
        'PO-DEMO-001',
        'Bande Démo Pesées',
        'PORC',
        'Large White',
        'ENGRAISSEMENT',
        v_structure_id,
        v_site_id,
        'INTERNE',
        50, 50,
        0, 0, 0,
        0, 0, 0,
        0,
        CURRENT_DATE - INTERVAL '90 days',
        'EN_COURS',
        25.000,
        2.200,
        'Bande de démonstration pour tester le bilan des pesées (GMQ ≈ 600 g/j)',
        now()
    )
    RETURNING id INTO v_bande_id;

    -- ── 3. INSÉRER LES PESÉES (1 toutes les 15 jours, croissance ~600 g/j) ──
    -- La 1ʳᵉ pesée n'a pas de référence précédente ; les suivantes référencent
    -- le poids et la date de la pesée d'avant (poids_precedent_kg / date_pesee_precedente).
    INSERT INTO pesees (
        bande_id, date_pesee, poids_kg,
        poids_precedent_kg, date_pesee_precedente,
        gain_depuis_derniere_pesee_kg, gmq_g,
        operateur_nom, notes, date_creation
    ) VALUES
        -- Pesée initiale : pas de référence antérieure
        (v_bande_id, CURRENT_DATE - 75, 27.500,
         NULL,            NULL,                 NULL,   NULL,
         'Opérateur Démo', 'Pesée initiale', now()),
        -- +15 j : +8,900 kg  -> GMQ = 8900/15 ≈ 593 g/j
        (v_bande_id, CURRENT_DATE - 60, 36.400,
         27.500,          CURRENT_DATE - 75,    8.900,  593.333,
         'Opérateur Démo', NULL, now()),
        -- +15 j : +8,700 kg  -> GMQ = 580 g/j
        (v_bande_id, CURRENT_DATE - 45, 45.100,
         36.400,          CURRENT_DATE - 60,    8.700,  580.000,
         'Opérateur Démo', NULL, now()),
        -- +15 j : +9,100 kg  -> GMQ ≈ 607 g/j
        (v_bande_id, CURRENT_DATE - 30, 54.200,
         45.100,          CURRENT_DATE - 45,    9.100,  606.667,
         'Opérateur Démo', NULL, now()),
        -- +15 j : +9,600 kg  -> GMQ = 640 g/j
        (v_bande_id, CURRENT_DATE - 15, 63.800,
         54.200,          CURRENT_DATE - 30,    9.600,  640.000,
         'Opérateur Démo', NULL, now()),
        -- Aujourd'hui : +9,100 kg -> GMQ ≈ 607 g/j (bouton "Prochaine pesée demain" actif)
        (v_bande_id, CURRENT_DATE,      72.900,
         63.800,          CURRENT_DATE - 15,    9.100,  606.667,
         'Opérateur Démo', 'Pesée du jour', now());

    -- ── 4. SYNCHRONISER LA BANDE (dernier poids, GMQ, IC estimé) ──
    -- IC estimé = (ration journalière × 15 jours) / gain par animal sur la période
    --           = (2.200 × 15) / 9.100 ≈ 3.626  -> arrondi de cohérence pour la démo
    UPDATE bandes
       SET poids_moyen_actuel_kg  = 72.900,
           gain_moyen_quotidien_g = 606.667,
           fcr_cumule             = 3.626
     WHERE id = v_bande_id;

    RAISE NOTICE '✅ Bande démo créée : id=% / code PO-DEMO-001 — 6 pesées insérées.', v_bande_id;
END $$;


-- ============================================================================
-- VÉRIFICATION (décommentez pour contrôler)
-- ============================================================================
-- SELECT b.code_bande, b.nom, b.poids_moyen_actuel_kg, b.gain_moyen_quotidien_g, b.fcr_cumule
--   FROM bandes b WHERE b.code_bande = 'PO-DEMO-001';
--
-- SELECT p.date_pesee, p.poids_kg, p.poids_precedent_kg, p.date_pesee_precedente,
--        p.gain_depuis_derniere_pesee_kg, p.gmq_g
--   FROM pesees p
--   JOIN bandes b ON b.id = p.bande_id
--  WHERE b.code_bande = 'PO-DEMO-001'
--  ORDER BY p.date_pesee ASC;
--
-- Nettoyage complet :
-- DELETE FROM pesees WHERE bande_id IN (SELECT id FROM bandes WHERE code_bande = 'PO-DEMO-001');
-- DELETE FROM bandes WHERE code_bande = 'PO-DEMO-001';
