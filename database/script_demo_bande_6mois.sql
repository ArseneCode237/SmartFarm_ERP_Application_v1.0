-- ============================================================================
-- SCRIPT DE TEST RÉEL : BANDE + PESÉES HEBDOMADAIRES SUR 6 MOIS
-- Base : PostgreSQL  |  smartfarm_db  |  Ferme : romualdFerme
--
-- Crée une bande avec ~26 pesées hebdomadaires sur 6 mois (croissance porcine
-- réaliste : 20 kg -> ~115 kg, GMQ moyen ~450-480 g/j avec variations).
-- Chaque pesée référence le poids et la date de la précédente.
--
-- Exécution :
--   psql -U postgres -d smartfarm_db -f script_demo_bande_6mois.sql
-- ============================================================================

DO $$
DECLARE
    v_structure_id BIGINT;
    v_site_id      BIGINT;
    v_bande_id     BIGINT;
    v_ferme_id     BIGINT;

    d_date_entree  DATE;
    d_date_pesee   DATE;
    d_poids        NUMERIC(8,3) := 20.000;   -- poids initial
    d_poids_prec   NUMERIC(8,3) := NULL;
    d_date_prec    DATE         := NULL;
    d_delta        NUMERIC(8,3);
    d_gmq          NUMERIC(8,3);
    i              INT          := 0;
    NB_PESEES      CONSTANT INT := 27;      -- 26 semaines + pesée initiale
BEGIN

    -- ── 1. FERME « romualdFerme » + structure active ──
    SELECT f.id INTO v_ferme_id
      FROM fermes f
     WHERE REPLACE(LOWER(f.nom), ' ', '') = 'romualdferme'
     LIMIT 1;

    IF v_ferme_id IS NULL THEN
        RAISE EXCEPTION 'Ferme « romualdFerme » introuvable. Vérifiez : SELECT id, nom FROM fermes;';
    END IF;

    SELECT s.id, s.site_id
      INTO v_structure_id, v_site_id
      FROM structures s
      JOIN sites si ON si.id = s.site_id
     WHERE si.ferme_id = v_ferme_id
       AND s.statut = 'ACTIF'
     ORDER BY s.id
     LIMIT 1;

    IF v_structure_id IS NULL THEN
        RAISE EXCEPTION 'Aucune structure active trouvée dans la ferme romualdFerme.';
    END IF;

    -- ── 2. NETTOYAGE + CRÉATION DE LA BANDE (entrée il y a 6 mois) ──
    DELETE FROM pesees WHERE bande_id IN (SELECT id FROM bandes WHERE code_bande = 'PO-DEMO-6M');
    DELETE FROM bandes WHERE code_bande = 'PO-DEMO-6M';

    d_date_entree := CURRENT_DATE - INTERVAL '6 months';

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
        'PO-DEMO-6M',
        'Bande Test 6 Mois',
        'PORC',
        'Large White',
        'ENGRAISSEMENT',
        v_structure_id,
        v_site_id,
        'INTERNE',
        40, 40,
        0, 0, 0,
        0, 0, 0,
        0,
        d_date_entree,
        'EN_COURS',
        20.000,
        2.500,
        'Bande de test réel — pesées hebdomadaires sur 6 mois (croissance complète d''engraissement)',
        now()
    )
    RETURNING id INTO v_bande_id;

    -- ── 3. GÉNÉRATION DES PESÉES HEBDOMADAIRES (boucle 26 semaines) ──
    -- Croissance réaliste : +4,2 kg/semaine en moyenne avec variation sinusoïdale
    -- (périodes légèrement plus rapides/lentes, comme en élevage réel).
    WHILE i < NB_PESEES LOOP
        IF i = 0 THEN
            -- Pesée initiale : pas de référence antérieure
            INSERT INTO pesees (
                bande_id, date_pesee, poids_kg,
                poids_precedent_kg, date_pesee_precedente,
                gain_depuis_derniere_pesee_kg, gmq_g,
                operateur_nom, notes, date_creation
            ) VALUES (
                v_bande_id, d_date_entree, d_poids,
                NULL, NULL, NULL, NULL,
                'Opérateur Démo', 'Pesée initiale (entrée en engraissement)', now()
            );
            d_date_prec := d_date_entree;
        ELSE
            d_date_pesee := d_date_entree + (i * 7);

            -- Gain hebdomadaire réaliste : 3,2 à 5,0 kg avec ondulation lente
            -- et léger ralentissement en fin de période (animaux proches du poids adulte)
            d_delta := ROUND((
                4.2
                + sin(i * 0.9) * 0.75          -- oscillation naturelle
                - GREATEST(0, (i - 18)) * 0.08 -- ralentissement après la semaine 18
            )::numeric, 1);

            d_poids := d_poids + d_delta;
            d_gmq   := ROUND((d_delta * 1000 / 7)::numeric, 3);

            INSERT INTO pesees (
                bande_id, date_pesee, poids_kg,
                poids_precedent_kg, date_pesee_precedente,
                gain_depuis_derniere_pesee_kg, gmq_g,
                operateur_nom, notes, date_creation
            ) VALUES (
                v_bande_id, d_date_pesee, d_poids,
                d_poids_prec, d_date_prec,
                d_delta, d_gmq,
                'Opérateur Démo',
                CASE WHEN d_date_pesee >= CURRENT_DATE THEN 'Pesée du jour' ELSE NULL END,
                now()
            );

            d_date_prec := d_date_pesee;
        END IF;

        d_poids_prec := d_poids;
        i := i + 1;
    END LOOP;

    -- Si la dernière pesée générée n'est pas aujourd'hui, ajouter la pesée du jour
    -- (pour tester le bouton « Prochaine pesée demain »)
    IF d_date_prec < CURRENT_DATE THEN
        d_date_pesee := CURRENT_DATE;
        d_delta      := ROUND((4.2 - GREATEST(0, (NB_PESEES - 18)) * 0.08)::numeric, 1);
        d_poids      := d_poids + d_delta;
        d_gmq        := ROUND((d_delta * 1000 / (CURRENT_DATE - d_date_prec))::numeric, 3);

        INSERT INTO pesees (
            bande_id, date_pesee, poids_kg,
            poids_precedent_kg, date_pesee_precedente,
            gain_depuis_derniere_pesee_kg, gmq_g,
            operateur_nom, notes, date_creation
        ) VALUES (
            v_bande_id, d_date_pesee, d_poids,
            d_poids_prec, d_date_prec,
            d_delta, d_gmq,
            'Opérateur Démo', 'Pesée du jour', now()
        );
        d_date_prec := d_date_pesee;
    END IF;

    -- ── 4. SYNCHRONISER LA BANDE AVEC LA DERNIÈRE PESÉE ──
    -- IC estimé = (ration journalière × jours depuis la dernière pesée) / gain par animal
    UPDATE bandes
       SET poids_moyen_actuel_kg  = d_poids,
           gain_moyen_quotidien_g = d_gmq,
           fcr_cumule             = ROUND(
               (2.500 * (CURRENT_DATE - d_date_prec) / GREATEST(d_delta, 0.001))::numeric, 3)
     WHERE id = v_bande_id;

    RAISE NOTICE '✅ Bande « PO-DEMO-6M » créée (id=%) — % pesées hebdomadaires sur 6 mois, poids final : % kg.',
                 v_bande_id, NB_PESEES, d_poids;
END $$;


-- ============================================================================
-- VÉRIFICATIONS (décommentez pour contrôler)
-- ============================================================================
-- SELECT b.code_bande, b.nom, b.date_entree,
--        b.poids_moyen_actuel_kg, b.gain_moyen_quotidien_g, b.fcr_cumule,
--        COUNT(p.id) AS nb_pesees,
--        MIN(p.date_pesee) AS premiere, MAX(p.date_pesee) AS derniere
--   FROM bandes b LEFT JOIN pesees p ON p.bande_id = b.id
--  WHERE b.code_bande = 'PO-DEMO-6M'
--  GROUP BY b.id;
--
-- SELECT date_pesee, poids_kg, poids_precedent_kg, gmq_g
--   FROM pesees p JOIN bandes b ON b.id = p.bande_id
--  WHERE b.code_bande = 'PO-DEMO-6M'
--  ORDER BY date_pesee ASC;
--
-- Nettoyage complet :
-- DELETE FROM pesees WHERE bande_id IN (SELECT id FROM bandes WHERE code_bande = 'PO-DEMO-6M');
-- DELETE FROM bandes WHERE code_bande = 'PO-DEMO-6M';
