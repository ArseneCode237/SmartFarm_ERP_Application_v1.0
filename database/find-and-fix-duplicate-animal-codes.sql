-- 1. Trouver les doublons dans code_rfid
SELECT code_rfid, COUNT(*) AS nb_doublons, ARRAY_AGG(id) AS ids_animaux
FROM animaux
WHERE code_rfid IS NOT NULL
GROUP BY code_rfid
HAVING COUNT(*) > 1;

-- 2. Trouver les doublons dans code_boucle
SELECT code_boucle, COUNT(*) AS nb_doublons, ARRAY_AGG(id) AS ids_animaux
FROM animaux
WHERE code_boucle IS NOT NULL
GROUP BY code_boucle
HAVING COUNT(*) > 1;

-- 3. Exemple de requête pour mettre à jour les doublons (remplacez les valeurs par les vôtres !)
-- UPDATE animaux SET code_rfid = NULL WHERE id = [ID_DU_DUPLICATA_A_GARDER_VIDE];
-- OU
-- UPDATE animaux SET code_rfid = 'RFID-0010-2' WHERE id = [ID_DU_DUPLICATA_A_MODIFIER];
