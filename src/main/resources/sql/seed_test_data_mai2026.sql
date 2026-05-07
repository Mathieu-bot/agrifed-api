-- ============================================================
-- Seed: Données de test - TD Final 6 Mai 2026
-- Source: PDF pages 24-29
-- Prérequis OBLIGATOIRES (dans cet ordre) :
--   1. db_init_v0.sql
--   2. migration_V0_0_1.sql
--   3. migration_v0_0_3.sql
--   4. migration_v0_0_4.sql
--   5. migration_v0_0_5.sql
--   6. migration_v0_0_6_mai2026.sql  (ajout relationship dans sponsorship)
--   7. seed_test_data.sql MODIFIÉ    (voir NOTE ci-dessous)
--   8. CE FICHIER
-- ============================================================
-- NOTE CRITIQUE sur seed_test_data.sql existant :
--   Le fichier seed_test_data.sql N'INSÈRE PAS membership_type dans member,
--   or la migration V0.0.5 rend cette colonne NOT NULL.
--   Il faut donc ajouter membership_type à chaque INSERT INTO member dans
--   seed_test_data.sql, OU exécuter l'UPDATE ci-dessous juste après seed_test_data.sql.
--   Ce seed le fait automatiquement en ÉTAPE 0.
-- ============================================================
-- DIVERGENCES STRUCTURE BD vs DONNÉES DE TEST (toutes corrigées ici) :
--
--  [1] member.membership_type (V0.0.5) NOT NULL mais absent du seed initial
--      → ÉTAPE 0 : UPDATE pour assigner le bon type selon le rôle tenu
--      Mapping : PRESIDENT→PRESIDENT, VICE_PRESIDENT→VICE_PRESIDENT,
--                TREASURER→TREASURER, SECRETARY→SECRETARY,
--                CONFIRMED_MEMBER→SENIOR, JUNIOR_MEMBER→JUNIOR
--
--  [2] account.type CHECK IN ('cash','bank','mobile_money') — MINUSCULES
--      → On insère 'bank' et 'mobile_money', PAS 'BANK' / 'MVOLA'
--
--  [3] account_extended.account_number VARCHAR(23) = BBBBBGGGGGCCCCCCCCCCCKKK
--      Le PDF donne 10 chiffres pour le numéro de compte, mais le format
--      exige 11 chiffres (CCCCCCCCCCC). On complète à 11 chiffres par
--      zero-padding à gauche pour respecter la contrainte VARCHAR(23).
--      BMOI : 00004 + 00001 + 12345678901 + 12 = 23 chars
--      BRED : 00008 + 00003 + 45678901234 + 58 = 23 chars
--      bank_code et branch_code stockés séparément (ajoutés par V0.0.3).
--
--  [4] contribution.type CHECK (V0.0.3) : 'ANNUALLY' (pas 'ANNUAL'),
--      'MONTHLY', 'PUNCTUALLY' → valeurs utilisées en cohérence
--
--  [5] membership_fee et collectivity_structure n'existent que post-migrations
--      V0.0.3 et V0.0.1 → déjà géré par seed_test_data.sql
--
--  [6] membership_date des anciens membres : ajustée à '2026-01-01' (page 27)
-- ============================================================

-- ============================================================
-- ÉTAPE 0 : Corrections sur les données déjà insérées
-- ============================================================

-- 0a. Assigner membership_type aux anciens membres (manquant dans seed initial)
--     Basé sur leur rôle dans collectivity_term pour 2026
UPDATE member SET membership_type = 'PRESIDENT'       WHERE id = 'C1-M1';  -- président col-1
UPDATE member SET membership_type = 'VICE_PRESIDENT'  WHERE id = 'C1-M2';  -- vice-président col-1
UPDATE member SET membership_type = 'SECRETARY'       WHERE id = 'C1-M3';  -- secrétaire col-1
UPDATE member SET membership_type = 'TREASURER'       WHERE id = 'C1-M4';  -- trésorier col-1
UPDATE member SET membership_type = 'PRESIDENT'       WHERE id = 'C1-M5';  -- président col-2
UPDATE member SET membership_type = 'VICE_PRESIDENT'  WHERE id = 'C1-M6';  -- vice-président col-2
UPDATE member SET membership_type = 'SECRETARY'       WHERE id = 'C1-M7';  -- secrétaire col-2
UPDATE member SET membership_type = 'TREASURER'       WHERE id = 'C1-M8';  -- trésorier col-2
UPDATE member SET membership_type = 'PRESIDENT'       WHERE id = 'C3-M1';  -- président col-3
UPDATE member SET membership_type = 'VICE_PRESIDENT'  WHERE id = 'C3-M2';  -- vice-président col-3
UPDATE member SET membership_type = 'SECRETARY'       WHERE id = 'C3-M3';  -- secrétaire col-3
UPDATE member SET membership_type = 'TREASURER'       WHERE id = 'C3-M4';  -- trésorier col-3
-- Membres confirmés (CONFIRMED_MEMBER dans context) → SENIOR dans membership_type
UPDATE member SET membership_type = 'SENIOR' WHERE id IN ('C1-M5','C1-M6','C1-M7','C1-M8',
                                                           'C3-M5','C3-M6','C3-M7','C3-M8');
-- Note : C1-M5 est PRESIDENT dans col-2 ET CONFIRMED dans col-1.
-- Un membre ne peut avoir qu'un membership_type. On prend son rôle le plus élevé.
-- C1-M5 → PRESIDENT, C1-M6 → VICE_PRESIDENT, C1-M7 → SECRETARY, C1-M8 → TREASURER
-- (les UPDATE SENIOR ci-dessus sont écrasés pour ces membres, ordre important)
-- Relancer les UPDATE de rôle spécifique après le SENIOR pour les membres bi-collectivité :
UPDATE member SET membership_type = 'PRESIDENT'      WHERE id = 'C1-M5';
UPDATE member SET membership_type = 'VICE_PRESIDENT' WHERE id = 'C1-M6';
UPDATE member SET membership_type = 'SECRETARY'      WHERE id = 'C1-M7';
UPDATE member SET membership_type = 'TREASURER'      WHERE id = 'C1-M8';

-- 0b. Ajuster membership_date de tous les anciens membres à 2026-01-01 (page 27)
UPDATE member SET membership_date = '2026-01-01'
WHERE id IN ('C1-M1','C1-M2','C1-M3','C1-M4','C1-M5','C1-M6','C1-M7','C1-M8',
             'C3-M1','C3-M2','C3-M3','C3-M4','C3-M5','C3-M6','C3-M7','C3-M8');

-- 0c. Ajuster start_date dans membership_history en cohérence
UPDATE membership_history SET start_date = '2026-01-01'
WHERE member_id IN ('C1-M1','C1-M2','C1-M3','C1-M4','C1-M5','C1-M6','C1-M7','C1-M8',
                    'C3-M1','C3-M2','C3-M3','C3-M4','C3-M5','C3-M6','C3-M7','C3-M8');

-- 0d. Supprimer les anciennes cotisations (cot-1, cot-2, cot-3 du seed initial)
--     membership_fee_id dans contribution passera à NULL via ON DELETE SET NULL (V0.0.3)
DELETE FROM membership_fee WHERE id IN ('cot-1', 'cot-2', 'cot-3');

-- ============================================================
-- 1. NOUVEAUX COMPTES FINANCIERS - Collectivité 3 (page 24)
-- ============================================================

-- 1a. Deux comptes bancaires pour col-3
--     account.type CHECK : 'cash' | 'bank' | 'mobile_money'  (MINUSCULES)
INSERT INTO account (id, type, collectivity_id, federation_id, balance)
VALUES
    ('C3-A-BANK-1', 'bank', 'col-3', NULL, 0),
    ('C3-A-BANK-2', 'bank', 'col-3', NULL, 0)
ON CONFLICT (id) DO NOTHING;

-- account_number = BBBBBGGGGGCCCCCCCCCCCKKK (exactement 23 chars)
-- PDF : numéro de compte à 10 chiffres → zero-pad à 11 pour respecter le format
-- BMOI : 00004 + 00001 + 12345678901 + 12  = 23 chars
-- BRED : 00008 + 00003 + 45678901234 + 58  = 23 chars
-- bank_code et branch_code : colonnes ajoutées par migration V0.0.3
INSERT INTO account_extended (account_id, holder_name, bank_name, account_number, rib_key, bank_code, branch_code)
VALUES
    ('C3-A-BANK-1', 'Koto',  'BMOI', '000040000112345678901 2', '12', '00004', '00001'),
    ('C3-A-BANK-2', 'Naivo', 'BRED', '000080000345678901234 8', '58', '00008', '00003')
ON CONFLICT (account_id) DO NOTHING;
-- Si votre application stocke account_number sans la clé RIB (21 chars), adaptez en conséquence.

-- 1b. Un compte mobile money MVOLA pour col-3
--     service_name CHECK : 'ORANGE_MONEY' | 'MVOLA' | 'AIRTEL_MONEY'  → 'MVOLA' est valide
INSERT INTO account (id, type, collectivity_id, federation_id, balance)
VALUES ('C3-A-MOBILE-1', 'mobile_money', 'col-3', NULL, 0)
ON CONFLICT (id) DO NOTHING;

INSERT INTO account_mobile (account_id, holder_name, service_name, phone_number)
VALUES ('C3-A-MOBILE-1', 'Kolo', 'MVOLA', '0341889612')
ON CONFLICT (account_id) DO NOTHING;

-- ============================================================
-- 2. NOUVELLES COTISATIONS (pages 25 - Tableaux 12, 13, 14)
-- membership_fee créée par migration V0.0.3
-- frequency CHECK : 'WEEKLY'|'MONTHLY'|'ANNUALLY'|'PUNCTUALLY'
-- status CHECK    : 'ACTIVE'|'INACTIVE'
-- ============================================================

-- 2a. Collectivité 1 - Tableau 12
INSERT INTO membership_fee (id, label, status, frequency, eligible_from, amount, collectivity_id)
VALUES
    ('cot-1', 'Cotisation annuelle', 'ACTIVE',  'ANNUALLY',   '2026-01-01', 200000.00, 'col-1'),
    ('cot-2', 'Famangiana',          'ACTIVE',  'PUNCTUALLY', '2026-04-30',  20000.00, 'col-1')
ON CONFLICT (id) DO NOTHING;

-- 2b. Collectivité 2 - Tableau 13
INSERT INTO membership_fee (id, label, status, frequency, eligible_from, amount, collectivity_id)
VALUES
    ('cot-3', 'Cotisation annuelle', 'ACTIVE',   'ANNUALLY', '2026-01-01', 200000.00, 'col-2'),
    ('cot-4', 'Cotisation 2025',     'INACTIVE', 'ANNUALLY', '2025-01-01', 100000.00, 'col-2')
ON CONFLICT (id) DO NOTHING;

-- 2c. Collectivité 3 - Tableau 14
INSERT INTO membership_fee (id, label, status, frequency, eligible_from, amount, collectivity_id)
VALUES
    ('cot-5', 'Cotisation mensuelle', 'ACTIVE', 'MONTHLY', '2026-04-01', 25000.00, 'col-3')
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 3. CONTRIBUTIONS ET TRANSACTIONS (pages 26-27 - Tableaux 15, 16, 17)
-- contribution.type CHECK (V0.0.3) : 'ANNUALLY' (pas 'ANNUAL'), 'MONTHLY', 'PUNCTUALLY'
-- payment_method CHECK : 'CASH' | 'BANK_TRANSFER' | 'MOBILE_BANKING'
-- collectivity_id nullable depuis V0.0.4, mais on le fournit toujours ici
-- ============================================================

-- ------------------------------------------------------------
-- 3a. Collectivité 1 - Tableau 15
-- ------------------------------------------------------------
INSERT INTO contribution (id, amount, collection_date, payment_method, type,
                          member_id, collectivity_id, membership_fee_id,
                          account_credited_id, creation_date, label)
VALUES
    ('con2-col1-m1', 200000.00, '2026-01-01', 'CASH',           'ANNUALLY', 'C1-M1', 'col-1', 'cot-1', 'C1-A-CASH',     '2026-01-01', 'Cotisation annuelle 2026'),
    ('con2-col1-m2', 200000.00, '2026-01-01', 'CASH',           'ANNUALLY', 'C1-M2', 'col-1', 'cot-1', 'C1-A-CASH',     '2026-01-01', 'Cotisation annuelle 2026'),
    ('con2-col1-m3', 200000.00, '2026-01-01', 'MOBILE_BANKING', 'ANNUALLY', 'C1-M3', 'col-1', 'cot-1', 'C1-A-MOBILE-1', '2026-01-01', 'Cotisation annuelle 2026'),
    ('con2-col1-m4', 200000.00, '2026-01-01', 'MOBILE_BANKING', 'ANNUALLY', 'C1-M4', 'col-1', 'cot-1', 'C1-A-MOBILE-1', '2026-01-01', 'Cotisation annuelle 2026'),
    ('con2-col1-m5', 150000.00, '2026-01-01', 'MOBILE_BANKING', 'ANNUALLY', 'C1-M5', 'col-1', 'cot-1', 'C1-A-MOBILE-1', '2026-01-01', 'Cotisation annuelle 2026 (partielle)'),
    ('con2-col1-m6', 100000.00, '2026-05-01', 'CASH',           'ANNUALLY', 'C1-M6', 'col-1', 'cot-1', 'C1-A-CASH',     '2026-05-01', 'Cotisation annuelle 2026 (partielle)'),
    ('con2-col1-m7',  60000.00, '2026-05-01', 'CASH',           'ANNUALLY', 'C1-M7', 'col-1', 'cot-1', 'C1-A-CASH',     '2026-05-01', 'Cotisation annuelle 2026 (partielle)'),
    ('con2-col1-m8',  90000.00, '2026-05-01', 'CASH',           'ANNUALLY', 'C1-M8', 'col-1', 'cot-1', 'C1-A-CASH',     '2026-05-01', 'Cotisation annuelle 2026 (partielle)')
ON CONFLICT (id) DO NOTHING;

INSERT INTO "transaction" (id, account_id, amount, transaction_date, description, member_id)
VALUES
    ('tr2-col1-m1', 'C1-A-CASH',     200000.00, '2026-01-01', 'Cotisation annuelle 2026',             'C1-M1'),
    ('tr2-col1-m2', 'C1-A-CASH',     200000.00, '2026-01-01', 'Cotisation annuelle 2026',             'C1-M2'),
    ('tr2-col1-m3', 'C1-A-MOBILE-1', 200000.00, '2026-01-01', 'Cotisation annuelle 2026',             'C1-M3'),
    ('tr2-col1-m4', 'C1-A-MOBILE-1', 200000.00, '2026-01-01', 'Cotisation annuelle 2026',             'C1-M4'),
    ('tr2-col1-m5', 'C1-A-MOBILE-1', 150000.00, '2026-01-01', 'Cotisation annuelle 2026 (partielle)', 'C1-M5'),
    ('tr2-col1-m6', 'C1-A-CASH',     100000.00, '2026-05-01', 'Cotisation annuelle 2026 (partielle)', 'C1-M6'),
    ('tr2-col1-m7', 'C1-A-CASH',      60000.00, '2026-05-01', 'Cotisation annuelle 2026 (partielle)', 'C1-M7'),
    ('tr2-col1-m8', 'C1-A-CASH',      90000.00, '2026-05-01', 'Cotisation annuelle 2026 (partielle)', 'C1-M8')
ON CONFLICT (id) DO NOTHING;

-- Soldes col-1 :
-- CASH     : 200k+200k + 100k+60k+90k = 650 000
-- MOBILE-1 : 200k+200k+150k           = 550 000
UPDATE account SET balance = 650000.00 WHERE id = 'C1-A-CASH';
UPDATE account SET balance = 550000.00 WHERE id = 'C1-A-MOBILE-1';

-- ------------------------------------------------------------
-- 3b. Collectivité 2 - Tableau 16
-- ------------------------------------------------------------
INSERT INTO contribution (id, amount, collection_date, payment_method, type,
                          member_id, collectivity_id, membership_fee_id,
                          account_credited_id, creation_date, label)
VALUES
    ('con2-col2-m1', 120000.00, '2026-01-01', 'CASH',           'ANNUALLY', 'C1-M1', 'col-2', 'cot-3', 'C2-A-CASH',     '2026-01-01', 'Cotisation annuelle 2026 (partielle)'),
    ('con2-col2-m2', 180000.00, '2026-01-01', 'CASH',           'ANNUALLY', 'C1-M2', 'col-2', 'cot-3', 'C2-A-CASH',     '2026-01-01', 'Cotisation annuelle 2026 (partielle)'),
    ('con2-col2-m3', 200000.00, '2026-01-01', 'CASH',           'ANNUALLY', 'C1-M3', 'col-2', 'cot-3', 'C2-A-CASH',     '2026-01-01', 'Cotisation annuelle 2026'),
    ('con2-col2-m4', 200000.00, '2026-01-01', 'CASH',           'ANNUALLY', 'C1-M4', 'col-2', 'cot-3', 'C2-A-CASH',     '2026-01-01', 'Cotisation annuelle 2026'),
    ('con2-col2-m5', 200000.00, '2026-01-01', 'CASH',           'ANNUALLY', 'C1-M5', 'col-2', 'cot-3', 'C2-A-CASH',     '2026-01-01', 'Cotisation annuelle 2026'),
    ('con2-col2-m6', 200000.00, '2026-01-01', 'CASH',           'ANNUALLY', 'C1-M6', 'col-2', 'cot-3', 'C2-A-CASH',     '2026-01-01', 'Cotisation annuelle 2026'),
    ('con2-col2-m7',  80000.00, '2026-01-01', 'MOBILE_BANKING', 'ANNUALLY', 'C1-M7', 'col-2', 'cot-3', 'C2-A-MOBILE-1', '2026-01-01', 'Cotisation annuelle 2026 (partielle)'),
    ('con2-col2-m8', 120000.00, '2026-01-01', 'MOBILE_BANKING', 'ANNUALLY', 'C1-M8', 'col-2', 'cot-3', 'C2-A-MOBILE-1', '2026-01-01', 'Cotisation annuelle 2026 (partielle)')
ON CONFLICT (id) DO NOTHING;

INSERT INTO "transaction" (id, account_id, amount, transaction_date, description, member_id)
VALUES
    ('tr2-col2-m1', 'C2-A-CASH',      120000.00, '2026-01-01', 'Cotisation annuelle 2026 (partielle)', 'C1-M1'),
    ('tr2-col2-m2', 'C2-A-CASH',      180000.00, '2026-01-01', 'Cotisation annuelle 2026 (partielle)', 'C1-M2'),
    ('tr2-col2-m3', 'C2-A-CASH',      200000.00, '2026-01-01', 'Cotisation annuelle 2026',             'C1-M3'),
    ('tr2-col2-m4', 'C2-A-CASH',      200000.00, '2026-01-01', 'Cotisation annuelle 2026',             'C1-M4'),
    ('tr2-col2-m5', 'C2-A-CASH',      200000.00, '2026-01-01', 'Cotisation annuelle 2026',             'C1-M5'),
    ('tr2-col2-m6', 'C2-A-CASH',      200000.00, '2026-01-01', 'Cotisation annuelle 2026',             'C1-M6'),
    ('tr2-col2-m7', 'C2-A-MOBILE-1',   80000.00, '2026-01-01', 'Cotisation annuelle 2026 (partielle)', 'C1-M7'),
    ('tr2-col2-m8', 'C2-A-MOBILE-1',  120000.00, '2026-01-01', 'Cotisation annuelle 2026 (partielle)', 'C1-M8')
ON CONFLICT (id) DO NOTHING;

-- Soldes col-2 :
-- CASH     : 120k+180k+200k×4 = 1 100 000
-- MOBILE-1 : 80k+120k         = 200 000
UPDATE account SET balance = 1100000.00 WHERE id = 'C2-A-CASH';
UPDATE account SET balance =  200000.00 WHERE id = 'C2-A-MOBILE-1';

-- ------------------------------------------------------------
-- 3c. Collectivité 3 - Tableau 17
-- Deux mois de cotisation mensuelle (avril + mai 2026)
-- Le PDF indique "BANK" comme moyen de paiement pour C3-M3/M4 en mai
-- mais le compte crédité est C3-A-MOBILE-1 → on utilise 'MOBILE_BANKING'
-- qui est cohérent avec le compte mobile money crédité.
-- ------------------------------------------------------------
INSERT INTO contribution (id, amount, collection_date, payment_method, type,
                          member_id, collectivity_id, membership_fee_id,
                          account_credited_id, creation_date, label)
VALUES
    -- Avril 2026
    ('con-col3-m1-apr', 25000.00, '2026-04-01', 'BANK_TRANSFER',  'MONTHLY', 'C3-M1', 'col-3', 'cot-5', 'C3-A-BANK-1',   '2026-04-01', 'Cotisation mensuelle avril 2026'),
    ('con-col3-m2-apr', 25000.00, '2026-04-01', 'BANK_TRANSFER',  'MONTHLY', 'C3-M2', 'col-3', 'cot-5', 'C3-A-BANK-1',   '2026-04-01', 'Cotisation mensuelle avril 2026'),
    ('con-col3-m3-apr', 25000.00, '2026-04-01', 'BANK_TRANSFER',  'MONTHLY', 'C3-M3', 'col-3', 'cot-5', 'C3-A-BANK-1',   '2026-04-01', 'Cotisation mensuelle avril 2026'),
    ('con-col3-m4-apr', 25000.00, '2026-04-01', 'BANK_TRANSFER',  'MONTHLY', 'C3-M4', 'col-3', 'cot-5', 'C3-A-BANK-1',   '2026-04-01', 'Cotisation mensuelle avril 2026'),
    ('con-col3-m5-apr', 25000.00, '2026-04-01', 'BANK_TRANSFER',  'MONTHLY', 'C3-M5', 'col-3', 'cot-5', 'C3-A-BANK-2',   '2026-04-01', 'Cotisation mensuelle avril 2026'),
    ('con-col3-m6-apr', 25000.00, '2026-04-01', 'BANK_TRANSFER',  'MONTHLY', 'C3-M6', 'col-3', 'cot-5', 'C3-A-BANK-2',   '2026-04-01', 'Cotisation mensuelle avril 2026'),
    ('con-col3-m7-apr', 25000.00, '2026-04-01', 'CASH',           'MONTHLY', 'C3-M7', 'col-3', 'cot-5', 'C3-A-CASH',     '2026-04-01', 'Cotisation mensuelle avril 2026'),
    ('con-col3-m8-apr', 25000.00, '2026-04-01', 'CASH',           'MONTHLY', 'C3-M8', 'col-3', 'cot-5', 'C3-A-CASH',     '2026-04-01', 'Cotisation mensuelle avril 2026'),
    -- Mai 2026
    ('con-col3-m1-may', 25000.00, '2026-05-01', 'BANK_TRANSFER',  'MONTHLY', 'C3-M1', 'col-3', 'cot-5', 'C3-A-BANK-1',   '2026-05-01', 'Cotisation mensuelle mai 2026'),
    ('con-col3-m2-may', 25000.00, '2026-05-01', 'BANK_TRANSFER',  'MONTHLY', 'C3-M2', 'col-3', 'cot-5', 'C3-A-BANK-1',   '2026-05-01', 'Cotisation mensuelle mai 2026'),
    ('con-col3-m3-may', 15000.00, '2026-05-01', 'MOBILE_BANKING', 'MONTHLY', 'C3-M3', 'col-3', 'cot-5', 'C3-A-MOBILE-1', '2026-05-01', 'Cotisation mensuelle mai 2026 (partielle)'),
    ('con-col3-m4-may', 15000.00, '2026-05-01', 'MOBILE_BANKING', 'MONTHLY', 'C3-M4', 'col-3', 'cot-5', 'C3-A-MOBILE-1', '2026-05-01', 'Cotisation mensuelle mai 2026 (partielle)'),
    ('con-col3-m5-may', 20000.00, '2026-05-01', 'BANK_TRANSFER',  'MONTHLY', 'C3-M5', 'col-3', 'cot-5', 'C3-A-BANK-2',   '2026-05-01', 'Cotisation mensuelle mai 2026 (partielle)'),
    ('con-col3-m6-may', 25000.00, '2026-05-01', 'BANK_TRANSFER',  'MONTHLY', 'C3-M6', 'col-3', 'cot-5', 'C3-A-BANK-2',   '2026-05-01', 'Cotisation mensuelle mai 2026'),
    ('con-col3-m7-may',  5000.00, '2026-05-01', 'CASH',           'MONTHLY', 'C3-M7', 'col-3', 'cot-5', 'C3-A-CASH',     '2026-05-01', 'Cotisation mensuelle mai 2026 (partielle)'),
    ('con-col3-m8-may',  5000.00, '2026-05-01', 'CASH',           'MONTHLY', 'C3-M8', 'col-3', 'cot-5', 'C3-A-CASH',     '2026-05-01', 'Cotisation mensuelle mai 2026 (partielle)')
ON CONFLICT (id) DO NOTHING;

INSERT INTO "transaction" (id, account_id, amount, transaction_date, description, member_id)
VALUES
    -- Avril
    ('tr-col3-m1-apr', 'C3-A-BANK-1',   25000.00, '2026-04-01', 'Cotisation mensuelle avril 2026',             'C3-M1'),
    ('tr-col3-m2-apr', 'C3-A-BANK-1',   25000.00, '2026-04-01', 'Cotisation mensuelle avril 2026',             'C3-M2'),
    ('tr-col3-m3-apr', 'C3-A-BANK-1',   25000.00, '2026-04-01', 'Cotisation mensuelle avril 2026',             'C3-M3'),
    ('tr-col3-m4-apr', 'C3-A-BANK-1',   25000.00, '2026-04-01', 'Cotisation mensuelle avril 2026',             'C3-M4'),
    ('tr-col3-m5-apr', 'C3-A-BANK-2',   25000.00, '2026-04-01', 'Cotisation mensuelle avril 2026',             'C3-M5'),
    ('tr-col3-m6-apr', 'C3-A-BANK-2',   25000.00, '2026-04-01', 'Cotisation mensuelle avril 2026',             'C3-M6'),
    ('tr-col3-m7-apr', 'C3-A-CASH',     25000.00, '2026-04-01', 'Cotisation mensuelle avril 2026',             'C3-M7'),
    ('tr-col3-m8-apr', 'C3-A-CASH',     25000.00, '2026-04-01', 'Cotisation mensuelle avril 2026',             'C3-M8'),
    -- Mai
    ('tr-col3-m1-may', 'C3-A-BANK-1',   25000.00, '2026-05-01', 'Cotisation mensuelle mai 2026',             'C3-M1'),
    ('tr-col3-m2-may', 'C3-A-BANK-1',   25000.00, '2026-05-01', 'Cotisation mensuelle mai 2026',             'C3-M2'),
    ('tr-col3-m3-may', 'C3-A-MOBILE-1', 15000.00, '2026-05-01', 'Cotisation mensuelle mai 2026 (partielle)', 'C3-M3'),
    ('tr-col3-m4-may', 'C3-A-MOBILE-1', 15000.00, '2026-05-01', 'Cotisation mensuelle mai 2026 (partielle)', 'C3-M4'),
    ('tr-col3-m5-may', 'C3-A-BANK-2',   20000.00, '2026-05-01', 'Cotisation mensuelle mai 2026 (partielle)', 'C3-M5'),
    ('tr-col3-m6-may', 'C3-A-BANK-2',   25000.00, '2026-05-01', 'Cotisation mensuelle mai 2026',             'C3-M6'),
    ('tr-col3-m7-may', 'C3-A-CASH',      5000.00, '2026-05-01', 'Cotisation mensuelle mai 2026 (partielle)', 'C3-M7'),
    ('tr-col3-m8-may', 'C3-A-CASH',      5000.00, '2026-05-01', 'Cotisation mensuelle mai 2026 (partielle)', 'C3-M8')
ON CONFLICT (id) DO NOTHING;

-- Soldes col-3 :
-- BANK-1   : (25k×4) + (25k+25k) = 100k + 50k   = 150 000
-- BANK-2   : (25k+25k) + (20k+25k) = 50k + 45k  =  95 000
-- MOBILE-1 : 15k+15k               =               30 000
-- CASH     : (25k+25k) + (5k+5k)   = 50k + 10k  =  60 000
UPDATE account SET balance = 150000.00 WHERE id = 'C3-A-BANK-1';
UPDATE account SET balance =  95000.00 WHERE id = 'C3-A-BANK-2';
UPDATE account SET balance =  30000.00 WHERE id = 'C3-A-MOBILE-1';
UPDATE account SET balance =  60000.00 WHERE id = 'C3-A-CASH';

-- ============================================================
-- 4. NOUVEAUX MEMBRES ADHÉRENTS (pages 27-29 - Tableaux 18, 19, 20)
-- membership_type CHECK (V0.0.5) : 'JUNIOR'|'SENIOR'|'SECRETARY'|
--                                   'TREASURER'|'VICE_PRESIDENT'|'PRESIDENT'
-- Tous les nouveaux sont JUNIOR.
-- Les données <random> sont remplies avec des valeurs fictives cohérentes.
-- ============================================================

-- ------------------------------------------------------------
-- 4a. Collectivité 1 - Tableau 18 (4 nouveaux juniors)
-- ------------------------------------------------------------
INSERT INTO member (id, lastname, firstname, birth_date, gender, address, occupation,
                    phone, email, membership_date,
                    registration_fee_paid, membership_dues_paid, membership_type)
VALUES
    ('C1-NM1', 'RandoA1', 'JuniorA1', '2000-01-15', 'MALE',   'Lot NM1 Ambato.', 'Agriculteur', '0340010001', 'nm1.col1@fed-agri.mg', '2026-04-01', TRUE, FALSE, 'JUNIOR'),
    ('C1-NM2', 'RandoA2', 'JuniorA2', '2001-03-20', 'FEMALE', 'Lot NM2 Ambato.', 'Agriculteur', '0340010002', 'nm2.col1@fed-agri.mg', '2026-04-01', TRUE, FALSE, 'JUNIOR'),
    ('C1-NM3', 'RandoA3', 'JuniorA3', '1999-07-10', 'MALE',   'Lot NM3 Ambato.', 'Agriculteur', '0340010003', 'nm3.col1@fed-agri.mg', '2026-05-01', TRUE, FALSE, 'JUNIOR'),
    ('C1-NM4', 'RandoA4', 'JuniorA4', '2002-11-05', 'MALE',   'Lot NM4 Ambato.', 'Agriculteur', '0340010004', 'nm4.col1@fed-agri.mg', '2026-06-01', TRUE, FALSE, 'JUNIOR')
ON CONFLICT (id) DO NOTHING;

INSERT INTO membership_history (id, start_date, end_date, reason, member_id, collectivity_id)
VALUES
    ('mh-col1-nm1', '2026-04-01', NULL, 'ADMISSION', 'C1-NM1', 'col-1'),
    ('mh-col1-nm2', '2026-04-01', NULL, 'ADMISSION', 'C1-NM2', 'col-1'),
    ('mh-col1-nm3', '2026-05-01', NULL, 'ADMISSION', 'C1-NM3', 'col-1'),
    ('mh-col1-nm4', '2026-06-01', NULL, 'ADMISSION', 'C1-NM4', 'col-1')
ON CONFLICT (id) DO NOTHING;

-- Parrains : C1-M1 et C1-M2 (confirmés dans col-1)
-- relationship : colonne ajoutée par migration V0.0.6
INSERT INTO sponsorship (id, sponsorship_date, sponsor_member_id, sponsored_member_id, relationship)
VALUES
    ('sp-col1-nm1-m1', '2026-04-01', 'C1-M1', 'C1-NM1', 'Amis'),
    ('sp-col1-nm1-m2', '2026-04-01', 'C1-M2', 'C1-NM1', 'Famille'),
    ('sp-col1-nm2-m1', '2026-04-01', 'C1-M1', 'C1-NM2', 'Collègues'),
    ('sp-col1-nm2-m2', '2026-04-01', 'C1-M2', 'C1-NM2', 'Amis'),
    ('sp-col1-nm3-m1', '2026-05-01', 'C1-M1', 'C1-NM3', 'Famille'),
    ('sp-col1-nm3-m2', '2026-05-01', 'C1-M2', 'C1-NM3', 'Amis'),
    ('sp-col1-nm4-m1', '2026-06-01', 'C1-M1', 'C1-NM4', 'Collègues'),
    ('sp-col1-nm4-m2', '2026-06-01', 'C1-M2', 'C1-NM4', 'Famille')
ON CONFLICT (id) DO NOTHING;

INSERT INTO collectivity_term (id, year, member_id, collectivity_id, position_id, vote_id)
VALUES
    ('ct-col1-nm1', 2026, 'C1-NM1', 'col-1', 'pos-junior-member', 'vote-col1-2026'),
    ('ct-col1-nm2', 2026, 'C1-NM2', 'col-1', 'pos-junior-member', 'vote-col1-2026'),
    ('ct-col1-nm3', 2026, 'C1-NM3', 'col-1', 'pos-junior-member', 'vote-col1-2026'),
    ('ct-col1-nm4', 2026, 'C1-NM4', 'col-1', 'pos-junior-member', 'vote-col1-2026')
ON CONFLICT (id) DO NOTHING;

-- ------------------------------------------------------------
-- 4b. Collectivité 2 - Tableau 19 (3 nouveaux juniors)
-- ------------------------------------------------------------
INSERT INTO member (id, lastname, firstname, birth_date, gender, address, occupation,
                    phone, email, membership_date,
                    registration_fee_paid, membership_dues_paid, membership_type)
VALUES
    ('C2-NM1', 'RandoB1', 'JuniorB1', '2000-05-12', 'FEMALE', 'Lot NM1 Ambato.', 'Agriculteur', '0340020001', 'nm1.col2@fed-agri.mg', '2026-03-01', TRUE, FALSE, 'JUNIOR'),
    ('C2-NM2', 'RandoB2', 'JuniorB2', '2001-09-25', 'MALE',   'Lot NM2 Ambato.', 'Agriculteur', '0340020002', 'nm2.col2@fed-agri.mg', '2026-03-01', TRUE, FALSE, 'JUNIOR'),
    ('C2-NM3', 'RandoB3', 'JuniorB3', '1999-12-30', 'MALE',   'Lot NM3 Ambato.', 'Agriculteur', '0340020003', 'nm3.col2@fed-agri.mg', '2026-03-01', TRUE, FALSE, 'JUNIOR')
ON CONFLICT (id) DO NOTHING;

INSERT INTO membership_history (id, start_date, end_date, reason, member_id, collectivity_id)
VALUES
    ('mh-col2-nm1', '2026-03-01', NULL, 'ADMISSION', 'C2-NM1', 'col-2'),
    ('mh-col2-nm2', '2026-03-01', NULL, 'ADMISSION', 'C2-NM2', 'col-2'),
    ('mh-col2-nm3', '2026-03-01', NULL, 'ADMISSION', 'C2-NM3', 'col-2')
ON CONFLICT (id) DO NOTHING;

-- Parrains : C1-M1 et C1-M2 (membres confirmés dans col-2 aussi)
INSERT INTO sponsorship (id, sponsorship_date, sponsor_member_id, sponsored_member_id, relationship)
VALUES
    ('sp-col2-nm1-m1', '2026-03-01', 'C1-M1', 'C2-NM1', 'Amis'),
    ('sp-col2-nm1-m2', '2026-03-01', 'C1-M2', 'C2-NM1', 'Famille'),
    ('sp-col2-nm2-m1', '2026-03-01', 'C1-M1', 'C2-NM2', 'Collègues'),
    ('sp-col2-nm2-m2', '2026-03-01', 'C1-M2', 'C2-NM2', 'Amis'),
    ('sp-col2-nm3-m1', '2026-03-01', 'C1-M1', 'C2-NM3', 'Famille'),
    ('sp-col2-nm3-m2', '2026-03-01', 'C1-M2', 'C2-NM3', 'Collègues')
ON CONFLICT (id) DO NOTHING;

INSERT INTO collectivity_term (id, year, member_id, collectivity_id, position_id, vote_id)
VALUES
    ('ct-col2-nm1', 2026, 'C2-NM1', 'col-2', 'pos-junior-member', 'vote-col2-2026'),
    ('ct-col2-nm2', 2026, 'C2-NM2', 'col-2', 'pos-junior-member', 'vote-col2-2026'),
    ('ct-col2-nm3', 2026, 'C2-NM3', 'col-2', 'pos-junior-member', 'vote-col2-2026')
ON CONFLICT (id) DO NOTHING;

-- ------------------------------------------------------------
-- 4c. Collectivité 3 - Tableau 20 (6 nouveaux juniors)
-- ------------------------------------------------------------
INSERT INTO member (id, lastname, firstname, birth_date, gender, address, occupation,
                    phone, email, membership_date,
                    registration_fee_paid, membership_dues_paid, membership_type)
VALUES
    ('C3-NM1', 'RandoC1', 'JuniorC1', '2000-02-18', 'MALE',   'Lot NM1 Antsirabe', 'Apiculteur', '0340030001', 'nm1.col3@fed-agri.mg', '2026-01-01', TRUE, FALSE, 'JUNIOR'),
    ('C3-NM2', 'RandoC2', 'JuniorC2', '2001-06-14', 'FEMALE', 'Lot NM2 Antsirabe', 'Apiculteur', '0340030002', 'nm2.col3@fed-agri.mg', '2026-02-01', TRUE, FALSE, 'JUNIOR'),
    ('C3-NM3', 'RandoC3', 'JuniorC3', '1999-04-22', 'MALE',   'Lot NM3 Antsirabe', 'Apiculteur', '0340030003', 'nm3.col3@fed-agri.mg', '2026-02-01', TRUE, FALSE, 'JUNIOR'),
    ('C3-NM4', 'RandoC4', 'JuniorC4', '2002-08-09', 'MALE',   'Lot NM4 Antsirabe', 'Apiculteur', '0340030004', 'nm4.col3@fed-agri.mg', '2026-03-01', TRUE, FALSE, 'JUNIOR'),
    ('C3-NM5', 'RandoC5', 'JuniorC5', '2000-10-03', 'FEMALE', 'Lot NM5 Antsirabe', 'Apiculteur', '0340030005', 'nm5.col3@fed-agri.mg', '2026-03-01', TRUE, FALSE, 'JUNIOR'),
    ('C3-NM6', 'RandoC6', 'JuniorC6', '2001-12-17', 'MALE',   'Lot NM6 Antsirabe', 'Apiculteur', '0340030006', 'nm6.col3@fed-agri.mg', '2026-03-01', TRUE, FALSE, 'JUNIOR')
ON CONFLICT (id) DO NOTHING;

INSERT INTO membership_history (id, start_date, end_date, reason, member_id, collectivity_id)
VALUES
    ('mh-col3-nm1', '2026-01-01', NULL, 'ADMISSION', 'C3-NM1', 'col-3'),
    ('mh-col3-nm2', '2026-02-01', NULL, 'ADMISSION', 'C3-NM2', 'col-3'),
    ('mh-col3-nm3', '2026-02-01', NULL, 'ADMISSION', 'C3-NM3', 'col-3'),
    ('mh-col3-nm4', '2026-03-01', NULL, 'ADMISSION', 'C3-NM4', 'col-3'),
    ('mh-col3-nm5', '2026-03-01', NULL, 'ADMISSION', 'C3-NM5', 'col-3'),
    ('mh-col3-nm6', '2026-03-01', NULL, 'ADMISSION', 'C3-NM6', 'col-3')
ON CONFLICT (id) DO NOTHING;

-- Parrains : C3-M1 et C3-M2
INSERT INTO sponsorship (id, sponsorship_date, sponsor_member_id, sponsored_member_id, relationship)
VALUES
    ('sp-col3-nm1-m1', '2026-01-01', 'C3-M1', 'C3-NM1', 'Amis'),
    ('sp-col3-nm1-m2', '2026-01-01', 'C3-M2', 'C3-NM1', 'Famille'),
    ('sp-col3-nm2-m1', '2026-02-01', 'C3-M1', 'C3-NM2', 'Collègues'),
    ('sp-col3-nm2-m2', '2026-02-01', 'C3-M2', 'C3-NM2', 'Amis'),
    ('sp-col3-nm3-m1', '2026-02-01', 'C3-M1', 'C3-NM3', 'Famille'),
    ('sp-col3-nm3-m2', '2026-02-01', 'C3-M2', 'C3-NM3', 'Collègues'),
    ('sp-col3-nm4-m1', '2026-03-01', 'C3-M1', 'C3-NM4', 'Amis'),
    ('sp-col3-nm4-m2', '2026-03-01', 'C3-M2', 'C3-NM4', 'Famille'),
    ('sp-col3-nm5-m1', '2026-03-01', 'C3-M1', 'C3-NM5', 'Collègues'),
    ('sp-col3-nm5-m2', '2026-03-01', 'C3-M2', 'C3-NM5', 'Amis'),
    ('sp-col3-nm6-m1', '2026-03-01', 'C3-M1', 'C3-NM6', 'Famille'),
    ('sp-col3-nm6-m2', '2026-03-01', 'C3-M2', 'C3-NM6', 'Collègues')
ON CONFLICT (id) DO NOTHING;

INSERT INTO collectivity_term (id, year, member_id, collectivity_id, position_id, vote_id)
VALUES
    ('ct-col3-nm1', 2026, 'C3-NM1', 'col-3', 'pos-junior-member', 'vote-col3-2026'),
    ('ct-col3-nm2', 2026, 'C3-NM2', 'col-3', 'pos-junior-member', 'vote-col3-2026'),
    ('ct-col3-nm3', 2026, 'C3-NM3', 'col-3', 'pos-junior-member', 'vote-col3-2026'),
    ('ct-col3-nm4', 2026, 'C3-NM4', 'col-3', 'pos-junior-member', 'vote-col3-2026'),
    ('ct-col3-nm5', 2026, 'C3-NM5', 'col-3', 'pos-junior-member', 'vote-col3-2026'),
    ('ct-col3-nm6', 2026, 'C3-NM6', 'col-3', 'pos-junior-member', 'vote-col3-2026')
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- END OF SEED - TD Final 6 Mai 2026
-- ============================================================
