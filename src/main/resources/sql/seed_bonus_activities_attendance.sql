-- ============================================================
-- Seed BONUS : Activités & Présences — TD Final 6 Mai 2026
-- Source   : PROG3-TD-Final-Mai-2026-Données-de-test-BONUS.pdf
-- Prérequis: db_init_v0.sql + migrations V0.0.1 → V0.0.6
--            + seed_test_data_mai2026.sql (données de base)
-- ============================================================
-- CORRECTIONS appliquées :
--   - IDs attendance ≤ 20 chars : format "a{N}-{MBR}-{MMDD}"
--     ex: a1-C1M1-0307 (13 chars)
--   - "30/04/3036" → '2026-04-30' (typo PDF)
--   - "PUNCTUAL"   → 'OTHER'      (absent du CHECK activity_type)
--   - "TRESAURER"  → 'TREASURER'  (coquille PDF)
--   - "0/04/2026"  → '2026-04-04' (date tronquée PDF)
-- ============================================================

-- ============================================================
-- 0. NETTOYAGE ciblé
-- ============================================================
DELETE FROM attendance          WHERE activity_id IN ('act-1','act-2','act-3','act-4','act-5','act-6','act-7');
DELETE FROM activity_occupation WHERE activity_id IN ('act-1','act-2','act-3','act-4','act-5','act-6','act-7');
DELETE FROM activity             WHERE id           IN ('act-1','act-2','act-3','act-4','act-5','act-6','act-7');

-- ============================================================
-- 1. ACTIVITÉS (Tableaux 21, 22, 23)
-- ============================================================

-- ---- Collectivité 1 ----
INSERT INTO activity (id, label, activity_type, executive_date,
                      recurrence_week_ordinal, recurrence_day_of_week,
                      collectivity_id, federation_id)
VALUES
    ('act-1', 'AG1',              'MEETING',  NULL,         1, 'SA', 'col-1', NULL),
    ('act-2', 'Formation de base','TRAINING', NULL,         2, 'SU', 'col-1', NULL);

-- ---- Collectivité 2 ----
INSERT INTO activity (id, label, activity_type, executive_date,
                      recurrence_week_ordinal, recurrence_day_of_week,
                      collectivity_id, federation_id)
VALUES
    ('act-3', 'AG2',              'MEETING',  NULL,         1, 'SU', 'col-2', NULL),
    ('act-4', 'Formation de base','TRAINING', NULL,         3, 'SU', 'col-2', NULL),
    ('act-5', 'Perfectionnement', 'OTHER',    '2026-04-30', NULL, NULL, 'col-2', NULL);

-- ---- Collectivité 3 ----
INSERT INTO activity (id, label, activity_type, executive_date,
                      recurrence_week_ordinal, recurrence_day_of_week,
                      collectivity_id, federation_id)
VALUES
    ('act-6', 'AG3',              'MEETING',  NULL,         1, 'FR', 'col-3', NULL),
    ('act-7', 'Formation de base','TRAINING', NULL,         4, 'WE', 'col-3', NULL);

-- ============================================================
-- 2. OCCUPATIONS CONCERNÉES (activity_occupation)
-- ============================================================

-- act-1 : AG1 col-1 → toutes occupations
INSERT INTO activity_occupation (activity_id, occupation) VALUES
    ('act-1','JUNIOR'),('act-1','SENIOR'),('act-1','SECRETARY'),
    ('act-1','TREASURER'),('act-1','VICE_PRESIDENT'),('act-1','PRESIDENT');

-- act-2 : Formation col-1 → JUNIOR
INSERT INTO activity_occupation (activity_id, occupation) VALUES ('act-2','JUNIOR');

-- act-3 : AG2 col-2 → toutes occupations
INSERT INTO activity_occupation (activity_id, occupation) VALUES
    ('act-3','JUNIOR'),('act-3','SENIOR'),('act-3','SECRETARY'),
    ('act-3','TREASURER'),('act-3','VICE_PRESIDENT'),('act-3','PRESIDENT');

-- act-4 : Formation col-2 → JUNIOR
INSERT INTO activity_occupation (activity_id, occupation) VALUES ('act-4','JUNIOR');

-- act-5 : Perfectionnement col-2 → SENIOR
INSERT INTO activity_occupation (activity_id, occupation) VALUES ('act-5','SENIOR');

-- act-6 : AG3 col-3 → toutes occupations
INSERT INTO activity_occupation (activity_id, occupation) VALUES
    ('act-6','JUNIOR'),('act-6','SENIOR'),('act-6','SECRETARY'),
    ('act-6','TREASURER'),('act-6','VICE_PRESIDENT'),('act-6','PRESIDENT');

-- act-7 : Formation col-3 → JUNIOR
INSERT INTO activity_occupation (activity_id, occupation) VALUES ('act-7','JUNIOR');

-- ============================================================
-- 3. PRÉSENCES (attendance)
-- Format ID : a{N}-{MBR}-{MMDD}  → max 20 caractères
--   ex: a1-C1M1-0307 = 13 chars ✓
-- is_external = TRUE si le membre n'appartient pas à la
--               collectivité organisatrice de l'activité.
-- ============================================================

-- ------------------------------------------------------------
-- Tableau 24 — AG1 (act-1) col-1, 07/03/2026
-- ------------------------------------------------------------
INSERT INTO attendance (id, occurrence_date, status, is_external, member_id, activity_id) VALUES
    ('a1-C1M1-0307', '2026-03-07', 'ATTENDED', FALSE, 'C1-M1', 'act-1'),
    ('a1-C1M2-0307', '2026-03-07', 'ATTENDED', FALSE, 'C1-M2', 'act-1'),
    ('a1-C1M3-0307', '2026-03-07', 'ATTENDED', FALSE, 'C1-M3', 'act-1'),
    ('a1-C1M4-0307', '2026-03-07', 'ATTENDED', FALSE, 'C1-M4', 'act-1'),
    ('a1-C1M5-0307', '2026-03-07', 'ATTENDED', FALSE, 'C1-M5', 'act-1'),
    ('a1-C1M6-0307', '2026-03-07', 'ATTENDED', FALSE, 'C1-M6', 'act-1'),
    ('a1-C1M7-0307', '2026-03-07', 'MISSING',  FALSE, 'C1-M7', 'act-1'),
    ('a1-C1M8-0307', '2026-03-07', 'MISSING',  FALSE, 'C1-M8', 'act-1');

-- ------------------------------------------------------------
-- Tableau 25 — AG1 (act-1) col-1, 04/04/2026
-- NOTE : "0/04/2026" dans le PDF pour C1-M1 → corrigé en 04/04/2026
-- ------------------------------------------------------------
INSERT INTO attendance (id, occurrence_date, status, is_external, member_id, activity_id) VALUES
    ('a1-C1M1-0404', '2026-04-04', 'ATTENDED', FALSE, 'C1-M1', 'act-1'),
    ('a1-C1M2-0404', '2026-04-04', 'ATTENDED', FALSE, 'C1-M2', 'act-1'),
    ('a1-C1M3-0404', '2026-04-04', 'MISSING',  FALSE, 'C1-M3', 'act-1'),
    ('a1-C1M4-0404', '2026-04-04', 'MISSING',  FALSE, 'C1-M4', 'act-1'),
    ('a1-C1M5-0404', '2026-04-04', 'ATTENDED', FALSE, 'C1-M5', 'act-1'),
    ('a1-C1M6-0404', '2026-04-04', 'ATTENDED', FALSE, 'C1-M6', 'act-1'),
    ('a1-C1M7-0404', '2026-04-04', 'ATTENDED', FALSE, 'C1-M7', 'act-1'),
    ('a1-C1M8-0404', '2026-04-04', 'ATTENDED', FALSE, 'C1-M8', 'act-1');

-- ------------------------------------------------------------
-- Tableau 26 — AG2 (act-3) col-2, 08/03/2026
-- ------------------------------------------------------------
INSERT INTO attendance (id, occurrence_date, status, is_external, member_id, activity_id) VALUES
    ('a3-C1M1-0308', '2026-03-08', 'ATTENDED', FALSE, 'C1-M1', 'act-3'),
    ('a3-C1M2-0308', '2026-03-08', 'ATTENDED', FALSE, 'C1-M2', 'act-3'),
    ('a3-C1M3-0308', '2026-03-08', 'MISSING',  FALSE, 'C1-M3', 'act-3'),
    ('a3-C1M4-0308', '2026-03-08', 'MISSING',  FALSE, 'C1-M4', 'act-3'),
    ('a3-C1M5-0308', '2026-03-08', 'ATTENDED', FALSE, 'C1-M5', 'act-3'),
    ('a3-C1M6-0308', '2026-03-08', 'ATTENDED', FALSE, 'C1-M6', 'act-3'),
    ('a3-C1M7-0308', '2026-03-08', 'ATTENDED', FALSE, 'C1-M7', 'act-3'),
    ('a3-C1M8-0308', '2026-03-08', 'ATTENDED', FALSE, 'C1-M8', 'act-3');

-- ------------------------------------------------------------
-- Tableau 27 — AG2 (act-3) col-2, 05/04/2026
-- ------------------------------------------------------------
INSERT INTO attendance (id, occurrence_date, status, is_external, member_id, activity_id) VALUES
    ('a3-C1M1-0405', '2026-04-05', 'ATTENDED', FALSE, 'C1-M1', 'act-3'),
    ('a3-C1M2-0405', '2026-04-05', 'ATTENDED', FALSE, 'C1-M2', 'act-3'),
    ('a3-C1M3-0405', '2026-04-05', 'MISSING',  FALSE, 'C1-M3', 'act-3'),
    ('a3-C1M4-0405', '2026-04-05', 'ATTENDED', FALSE, 'C1-M4', 'act-3'),
    ('a3-C1M5-0405', '2026-04-05', 'ATTENDED', FALSE, 'C1-M5', 'act-3'),
    ('a3-C1M6-0405', '2026-04-05', 'ATTENDED', FALSE, 'C1-M6', 'act-3'),
    ('a3-C1M7-0405', '2026-04-05', 'ATTENDED', FALSE, 'C1-M7', 'act-3'),
    ('a3-C1M8-0405', '2026-04-05', 'MISSING',  FALSE, 'C1-M8', 'act-3');

-- ------------------------------------------------------------
-- Tableau 28 — Perfectionnement (act-5) col-2, 30/04/2026
-- ------------------------------------------------------------
INSERT INTO attendance (id, occurrence_date, status, is_external, member_id, activity_id) VALUES
    ('a5-C1M1-0430', '2026-04-30', 'ATTENDED',  FALSE, 'C1-M1', 'act-5'),
    ('a5-C1M2-0430', '2026-04-30', 'ATTENDED',  FALSE, 'C1-M2', 'act-5'),
    ('a5-C1M3-0430', '2026-04-30', 'ATTENDED',  FALSE, 'C1-M3', 'act-5'),
    ('a5-C1M4-0430', '2026-04-30', 'MISSING',   FALSE, 'C1-M4', 'act-5'),
    ('a5-C1M5-0430', '2026-04-30', 'UNDEFINED', FALSE, 'C1-M5', 'act-5'),
    ('a5-C1M6-0430', '2026-04-30', 'UNDEFINED', FALSE, 'C1-M6', 'act-5'),
    ('a5-C1M7-0430', '2026-04-30', 'UNDEFINED', FALSE, 'C1-M7', 'act-5'),
    ('a5-C1M8-0430', '2026-04-30', 'UNDEFINED', FALSE, 'C1-M8', 'act-5');

-- ------------------------------------------------------------
-- Tableau 29 — AG3 (act-6) col-3, 06/03/2026
-- NOTE : "C3-M8 Nom membre 9" dans le PDF est une coquille de libellé ;
--        on conserve l'ID C3-M8 (Nom membre 16) tel que défini dans le seed.
-- ------------------------------------------------------------
INSERT INTO attendance (id, occurrence_date, status, is_external, member_id, activity_id) VALUES
    ('a6-C3M1-0306', '2026-03-06', 'ATTENDED', FALSE, 'C3-M1', 'act-6'),
    ('a6-C3M2-0306', '2026-03-06', 'ATTENDED', FALSE, 'C3-M2', 'act-6'),
    ('a6-C3M3-0306', '2026-03-06', 'ATTENDED', FALSE, 'C3-M3', 'act-6'),
    ('a6-C3M4-0306', '2026-03-06', 'ATTENDED', FALSE, 'C3-M4', 'act-6'),
    ('a6-C3M5-0306', '2026-03-06', 'ATTENDED', FALSE, 'C3-M5', 'act-6'),
    ('a6-C3M6-0306', '2026-03-06', 'ATTENDED', FALSE, 'C3-M6', 'act-6'),
    ('a6-C3M7-0306', '2026-03-06', 'MISSING',  FALSE, 'C3-M7', 'act-6'),
    ('a6-C3M8-0306', '2026-03-06', 'MISSING',  FALSE, 'C3-M8', 'act-6');

-- ------------------------------------------------------------
-- Tableau 30 — AG3 (act-6) col-3, 03/04/2026
-- C1-M1 est externe à col-3 → is_external = TRUE
-- ------------------------------------------------------------
INSERT INTO attendance (id, occurrence_date, status, is_external, member_id, activity_id) VALUES
    ('a6-C3M1-0403', '2026-04-03', 'ATTENDED', FALSE, 'C3-M1', 'act-6'),
    ('a6-C3M2-0403', '2026-04-03', 'ATTENDED', FALSE, 'C3-M2', 'act-6'),
    ('a6-C3M3-0403', '2026-04-03', 'MISSING',  FALSE, 'C3-M3', 'act-6'),
    ('a6-C3M4-0403', '2026-04-03', 'MISSING',  FALSE, 'C3-M4', 'act-6'),
    ('a6-C3M5-0403', '2026-04-03', 'ATTENDED', FALSE, 'C3-M5', 'act-6'),
    ('a6-C3M6-0403', '2026-04-03', 'ATTENDED', FALSE, 'C3-M6', 'act-6'),
    ('a6-C3M7-0403', '2026-04-03', 'MISSING',  FALSE, 'C3-M7', 'act-6'),
    ('a6-C3M8-0403', '2026-04-03', 'ATTENDED', FALSE, 'C3-M8', 'act-6'),
    ('a6-C1M1-0403', '2026-04-03', 'ATTENDED', TRUE,  'C1-M1', 'act-6');

-- ============================================================
-- END OF SEED BONUS — Activités & Présences
-- ============================================================
