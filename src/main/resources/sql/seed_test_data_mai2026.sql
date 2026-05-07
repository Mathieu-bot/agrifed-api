-- ============================================================
-- Seed : Données de test - TD Final 6 Mai 2026
-- Source : PDF pages 24-29
-- Basé sur : db_init_v0.sql + migrations V0.0.1 → V0.0.6
-- ============================================================
-- INSTRUCTIONS :
--   1. Exécuter db_init_v0.sql (DDL) avant ce fichier
--   2. Exécuter toutes les migrations (V0.0.1 → V0.0.6) avant ce fichier
--   3. Ce fichier repart de zéro : TRUNCATE complet puis réinsertion
-- ============================================================

-- ============================================================
-- 0. NETTOYAGE COMPLET (ordre inverse des dépendances FK)
-- ============================================================
TRUNCATE TABLE attendance              CASCADE;
TRUNCATE TABLE activity                CASCADE;
TRUNCATE TABLE "transaction"           CASCADE;
TRUNCATE TABLE account_mobile          CASCADE;
TRUNCATE TABLE account_extended        CASCADE;
TRUNCATE TABLE account                 CASCADE;
TRUNCATE TABLE contribution            CASCADE;
TRUNCATE TABLE membership_fee          CASCADE;
TRUNCATE TABLE sponsorship             CASCADE;
TRUNCATE TABLE collectivity_term       CASCADE;
TRUNCATE TABLE collectivity_vote       CASCADE;
TRUNCATE TABLE collectivity_structure  CASCADE;
TRUNCATE TABLE membership_history      CASCADE;
TRUNCATE TABLE collectivity            CASCADE;
TRUNCATE TABLE federation_term         CASCADE;
TRUNCATE TABLE federation_vote         CASCADE;
TRUNCATE TABLE position                CASCADE;
TRUNCATE TABLE member                  CASCADE;
TRUNCATE TABLE federation              CASCADE;

-- ============================================================
-- 1. FEDERATION
-- ============================================================
INSERT INTO federation (id, name)
VALUES ('fed-1', 'Fédération de collectivités agricoles');

-- ============================================================
-- 2. POSITIONS (référentiel complet)
-- ============================================================
INSERT INTO position (id, label, context)
VALUES
    ('pos-president',        'PRESIDENT',        'BOTH'),
    ('pos-vice-president',   'VICE_PRESIDENT',   'BOTH'),
    ('pos-treasurer',        'TREASURER',        'BOTH'),
    ('pos-secretary',        'SECRETARY',        'BOTH'),
    ('pos-confirmed-member', 'CONFIRMED_MEMBER', 'COLLECTIVITY'),
    ('pos-junior-member',    'JUNIOR_MEMBER',    'COLLECTIVITY');

-- ============================================================
-- 3. COLLECTIVITES (Tableau 1, page 14)
-- ============================================================
INSERT INTO collectivity (id, number, name, specialty, city, creation_date, federation_id,
                          status, location, federation_approval)
VALUES
    ('col-1', '1', 'Mpanorina',      'Riziculture', 'Ambatondrazaka', '2020-01-01', 'fed-1', 'APPROVED', 'Ambatondrazaka', TRUE),
    ('col-2', '2', 'Dobo voalohany', 'Pisciculture', 'Ambatondrazaka', '2020-01-01', 'fed-1', 'APPROVED', 'Ambatondrazaka', TRUE),
    ('col-3', '3', 'Tantely mamy',   'Apiculture',   'Brickaville',   '2020-01-01', 'fed-1', 'APPROVED', 'Brickaville',   TRUE);

-- ============================================================
-- 4. MEMBRES (Tableaux 2, 3 et 4 — pages 15-17)
-- membership_date = 01/01/2026 pour TOUS les anciens membres (consigne page 27)
-- membership_type ajouté par migration V0.0.5
-- ============================================================

-- Membres partagés col-1 & col-2 (C1-M1 à C1-M8)
INSERT INTO member (id, lastname, firstname, birth_date, gender, address, occupation,
                    phone, email, membership_date,
                    registration_fee_paid, membership_dues_paid, membership_type)
VALUES
    ('C1-M1', 'Nom membre 1',  'Prénom membre 1',  '1980-02-01', 'MALE',   'Lot II V M Ambato.',  'Riziculteur',  '0341234567', 'member.1@fed-agri.mg',  '2026-01-01', TRUE, TRUE,  'PRESIDENT'),
    ('C1-M2', 'Nom membre 2',  'Prénom membre 2',  '1982-03-05', 'MALE',   'Lot II F Ambato.',    'Agriculteur',  '0321234567', 'member.2@fed-agri.mg',  '2026-01-01', TRUE, TRUE,  'VICE_PRESIDENT'),
    ('C1-M3', 'Nom membre 3',  'Prénom membre 3',  '1992-03-10', 'MALE',   'Lot II J Ambato.',    'Collecteur',   '0331234567', 'member.3@fed-agri.mg',  '2026-01-01', TRUE, TRUE,  'SECRETARY'),
    ('C1-M4', 'Nom membre 4',  'Prénom membre 4',  '1988-05-22', 'FEMALE', 'Lot A K 50 Ambato.',  'Distributeur', '0381234567', 'member.4@fed-agri.mg',  '2026-01-01', TRUE, TRUE,  'TREASURER'),
    ('C1-M5', 'Nom membre 5',  'Prénom membre 5',  '1999-08-21', 'MALE',   'Lot UV 80 Ambato.',   'Riziculteur',  '0373434567', 'member.5@fed-agri.mg',  '2026-01-01', TRUE, TRUE,  'PRESIDENT'),
    ('C1-M6', 'Nom membre 6',  'Prénom membre 6',  '1998-08-22', 'FEMALE', 'Lot UV 6 Ambato.',    'Riziculteur',  '0372234567', 'member.6@fed-agri.mg',  '2026-01-01', TRUE, TRUE,  'VICE_PRESIDENT'),
    ('C1-M7', 'Nom membre 7',  'Prénom membre 7',  '1998-01-31', 'MALE',   'Lot UV 7 Ambato.',    'Riziculteur',  '0374234567', 'member.7@fed-agri.mg',  '2026-01-01', TRUE, FALSE, 'SECRETARY'),
    ('C1-M8', 'Nom membre 8',  'Prénom membre 8',  '1975-08-20', 'MALE',   'Lot UV 8 Ambato.',    'Riziculteur',  '0370234567', 'member.8@fed-agri.mg',  '2026-01-01', TRUE, FALSE, 'TREASURER');

-- Membres col-3 (C3-M1 à C3-M8)
INSERT INTO member (id, lastname, firstname, birth_date, gender, address, occupation,
                    phone, email, membership_date,
                    registration_fee_paid, membership_dues_paid, membership_type)
VALUES
    ('C3-M1', 'Nom membre 9',  'Prénom membre 9',  '1988-01-02', 'MALE',   'Lot 33 J Antsirabe',   'Apiculteur',   '034034567',  'member.9@fed-agri.mg',  '2026-01-01', TRUE, TRUE,  'PRESIDENT'),
    ('C3-M2', 'Nom membre 10', 'Prénom membre 10', '1982-03-05', 'MALE',   'Lot 2 J Antsirabe',    'Agriculteur',  '0338634567', 'member.10@fed-agri.mg', '2026-01-01', TRUE, TRUE,  'VICE_PRESIDENT'),
    ('C3-M3', 'Nom membre 11', 'Prénom membre 11', '1992-03-12', 'MALE',   'Lot 8 KM Antsirabe',   'Collecteur',   '0338234567', 'member.11@fed-agri.mg', '2026-01-01', TRUE, TRUE,  'SECRETARY'),
    ('C3-M4', 'Nom membre 12', 'Prénom membre 12', '1988-05-10', 'FEMALE', 'Lot A K 50 Antsirabe', 'Distributeur', '0382334567', 'member.12@fed-agri.mg', '2026-01-01', TRUE, TRUE,  'TREASURER'),
    ('C3-M5', 'Nom membre 13', 'Prénom membre 13', '1999-08-11', 'MALE',   'Lot UV 80 Antsirabe',  'Apiculteur',   '0373365567', 'member.13@fed-agri.mg', '2026-01-01', TRUE, TRUE,  'SENIOR'),
    ('C3-M6', 'Nom membre 14', 'Prénom membre 14', '1998-08-09', 'FEMALE', 'Lot UV 6 Antsirabe',   'Apiculteur',   '0378234567', 'member.14@fed-agri.mg', '2026-01-01', TRUE, TRUE,  'SENIOR'),
    ('C3-M7', 'Nom membre 15', 'Prénom membre 15', '1998-01-13', 'MALE',   'Lot UV 7 Antsirabe',   'Apiculteur',   '0374914567', 'member.15@fed-agri.mg', '2026-01-01', TRUE, TRUE,  'SENIOR'),
    ('C3-M8', 'Nom membre 16', 'Prénom membre 16', '1975-08-02', 'MALE',   'Lot UV 8 Antsirabe',   'Apiculteur',   '0370634567', 'member.16@fed-agri.mg', '2026-01-01', TRUE, TRUE,  'SENIOR');

-- ============================================================
-- 5. SPONSORSHIPS (Tableaux 2, 3 et 4)
-- relationship ajouté par migration V0.0.6 (nullable)
-- ============================================================

-- Parrainages col-1 (Tableau 2)
INSERT INTO sponsorship (id, sponsorship_date, sponsor_member_id, sponsored_member_id, relationship)
VALUES
    ('sp-c1m3-m1', '2026-01-01', 'C1-M1', 'C1-M3', 'collègues'),
    ('sp-c1m3-m2', '2026-01-01', 'C1-M2', 'C1-M3', 'collègues'),
    ('sp-c1m4-m1', '2026-01-01', 'C1-M1', 'C1-M4', 'collègues'),
    ('sp-c1m4-m2', '2026-01-01', 'C1-M2', 'C1-M4', 'collègues'),
    ('sp-c1m5-m1', '2026-01-01', 'C1-M1', 'C1-M5', 'collègues'),
    ('sp-c1m5-m2', '2026-01-01', 'C1-M2', 'C1-M5', 'collègues'),
    ('sp-c1m6-m1', '2026-01-01', 'C1-M1', 'C1-M6', 'collègues'),
    ('sp-c1m6-m2', '2026-01-01', 'C1-M2', 'C1-M6', 'collègues'),
    ('sp-c1m7-m1', '2026-01-01', 'C1-M1', 'C1-M7', 'collègues'),
    ('sp-c1m7-m2', '2026-01-01', 'C1-M2', 'C1-M7', 'collègues'),
    ('sp-c1m8-m6', '2026-01-01', 'C1-M6', 'C1-M8', 'collègues'),
    ('sp-c1m8-m7', '2026-01-01', 'C1-M7', 'C1-M8', 'collègues');

-- Parrainages col-2 (Tableau 3 — mêmes membres, parrains identiques)
INSERT INTO sponsorship (id, sponsorship_date, sponsor_member_id, sponsored_member_id, relationship)
VALUES
    ('sp-c2m3-m1', '2026-01-01', 'C1-M1', 'C1-M3', 'collègues'),
    ('sp-c2m3-m2', '2026-01-01', 'C1-M2', 'C1-M3', 'collègues'),
    ('sp-c2m4-m1', '2026-01-01', 'C1-M1', 'C1-M4', 'collègues'),
    ('sp-c2m4-m2', '2026-01-01', 'C1-M2', 'C1-M4', 'collègues'),
    ('sp-c2m5-m1', '2026-01-01', 'C1-M1', 'C1-M5', 'collègues'),
    ('sp-c2m5-m2', '2026-01-01', 'C1-M2', 'C1-M5', 'collègues'),
    ('sp-c2m6-m1', '2026-01-01', 'C1-M1', 'C1-M6', 'collègues'),
    ('sp-c2m6-m2', '2026-01-01', 'C1-M2', 'C1-M6', 'collègues'),
    ('sp-c2m7-m1', '2026-01-01', 'C1-M1', 'C1-M7', 'collègues'),
    ('sp-c2m7-m2', '2026-01-01', 'C1-M2', 'C1-M7', 'collègues'),
    ('sp-c2m8-m6', '2026-01-01', 'C1-M6', 'C1-M8', 'collègues'),
    ('sp-c2m8-m7', '2026-01-01', 'C1-M7', 'C1-M8', 'collègues');

-- Parrainages col-3 (Tableau 4)
INSERT INTO sponsorship (id, sponsorship_date, sponsor_member_id, sponsored_member_id, relationship)
VALUES
    ('sp-c3m3-m1', '2026-01-01', 'C3-M1', 'C3-M3', 'collègues'),
    ('sp-c3m3-m2', '2026-01-01', 'C3-M2', 'C3-M3', 'collègues'),
    ('sp-c3m4-m1', '2026-01-01', 'C3-M1', 'C3-M4', 'collègues'),
    ('sp-c3m4-m2', '2026-01-01', 'C3-M2', 'C3-M4', 'collègues'),
    ('sp-c3m5-m1', '2026-01-01', 'C3-M1', 'C3-M5', 'collègues'),
    ('sp-c3m5-m2', '2026-01-01', 'C3-M2', 'C3-M5', 'collègues'),
    ('sp-c3m6-m1', '2026-01-01', 'C3-M1', 'C3-M6', 'collègues'),
    ('sp-c3m6-m2', '2026-01-01', 'C3-M2', 'C3-M6', 'collègues'),
    ('sp-c3m7-m1', '2026-01-01', 'C3-M1', 'C3-M7', 'collègues'),
    ('sp-c3m7-m2', '2026-01-01', 'C3-M2', 'C3-M7', 'collègues'),
    ('sp-c3m8-m1', '2026-01-01', 'C3-M1', 'C3-M8', 'collègues'),
    ('sp-c3m8-m2', '2026-01-01', 'C3-M2', 'C3-M8', 'collègues');

-- ============================================================
-- 6. MEMBERSHIP HISTORY (anciens membres — date 01/01/2026)
-- ============================================================
INSERT INTO membership_history (id, start_date, end_date, reason, member_id, collectivity_id)
VALUES
    -- col-1
    ('mh-col1-m1', '2026-01-01', NULL, 'ADMISSION', 'C1-M1', 'col-1'),
    ('mh-col1-m2', '2026-01-01', NULL, 'ADMISSION', 'C1-M2', 'col-1'),
    ('mh-col1-m3', '2026-01-01', NULL, 'ADMISSION', 'C1-M3', 'col-1'),
    ('mh-col1-m4', '2026-01-01', NULL, 'ADMISSION', 'C1-M4', 'col-1'),
    ('mh-col1-m5', '2026-01-01', NULL, 'ADMISSION', 'C1-M5', 'col-1'),
    ('mh-col1-m6', '2026-01-01', NULL, 'ADMISSION', 'C1-M6', 'col-1'),
    ('mh-col1-m7', '2026-01-01', NULL, 'ADMISSION', 'C1-M7', 'col-1'),
    ('mh-col1-m8', '2026-01-01', NULL, 'ADMISSION', 'C1-M8', 'col-1'),
    -- col-2 (mêmes membres physiques)
    ('mh-col2-m1', '2026-01-01', NULL, 'ADMISSION', 'C1-M1', 'col-2'),
    ('mh-col2-m2', '2026-01-01', NULL, 'ADMISSION', 'C1-M2', 'col-2'),
    ('mh-col2-m3', '2026-01-01', NULL, 'ADMISSION', 'C1-M3', 'col-2'),
    ('mh-col2-m4', '2026-01-01', NULL, 'ADMISSION', 'C1-M4', 'col-2'),
    ('mh-col2-m5', '2026-01-01', NULL, 'ADMISSION', 'C1-M5', 'col-2'),
    ('mh-col2-m6', '2026-01-01', NULL, 'ADMISSION', 'C1-M6', 'col-2'),
    ('mh-col2-m7', '2026-01-01', NULL, 'ADMISSION', 'C1-M7', 'col-2'),
    ('mh-col2-m8', '2026-01-01', NULL, 'ADMISSION', 'C1-M8', 'col-2'),
    -- col-3
    ('mh-col3-m1', '2026-01-01', NULL, 'ADMISSION', 'C3-M1', 'col-3'),
    ('mh-col3-m2', '2026-01-01', NULL, 'ADMISSION', 'C3-M2', 'col-3'),
    ('mh-col3-m3', '2026-01-01', NULL, 'ADMISSION', 'C3-M3', 'col-3'),
    ('mh-col3-m4', '2026-01-01', NULL, 'ADMISSION', 'C3-M4', 'col-3'),
    ('mh-col3-m5', '2026-01-01', NULL, 'ADMISSION', 'C3-M5', 'col-3'),
    ('mh-col3-m6', '2026-01-01', NULL, 'ADMISSION', 'C3-M6', 'col-3'),
    ('mh-col3-m7', '2026-01-01', NULL, 'ADMISSION', 'C3-M7', 'col-3'),
    ('mh-col3-m8', '2026-01-01', NULL, 'ADMISSION', 'C3-M8', 'col-3');

-- ============================================================
-- 7. COLLECTIVITY VOTES (requis par FK collectivity_term)
-- ============================================================
INSERT INTO collectivity_vote (id, vote_date, target_year, collectivity_id)
VALUES
    ('vote-col1-2026', '2025-12-15', 2026, 'col-1'),
    ('vote-col2-2026', '2025-12-15', 2026, 'col-2'),
    ('vote-col3-2026', '2025-12-15', 2026, 'col-3');

-- ============================================================
-- 8. COLLECTIVITY TERMS (mandats 2026)
-- ============================================================

-- col-1 (Tableau 2)
INSERT INTO collectivity_term (id, year, member_id, collectivity_id, position_id, vote_id)
VALUES
    ('ct-col1-m1', 2026, 'C1-M1', 'col-1', 'pos-president',        'vote-col1-2026'),
    ('ct-col1-m2', 2026, 'C1-M2', 'col-1', 'pos-vice-president',   'vote-col1-2026'),
    ('ct-col1-m3', 2026, 'C1-M3', 'col-1', 'pos-secretary',        'vote-col1-2026'),
    ('ct-col1-m4', 2026, 'C1-M4', 'col-1', 'pos-treasurer',        'vote-col1-2026'),
    ('ct-col1-m5', 2026, 'C1-M5', 'col-1', 'pos-confirmed-member', 'vote-col1-2026'),
    ('ct-col1-m6', 2026, 'C1-M6', 'col-1', 'pos-confirmed-member', 'vote-col1-2026'),
    ('ct-col1-m7', 2026, 'C1-M7', 'col-1', 'pos-confirmed-member', 'vote-col1-2026'),
    ('ct-col1-m8', 2026, 'C1-M8', 'col-1', 'pos-confirmed-member', 'vote-col1-2026');

-- col-2 (Tableau 3)
INSERT INTO collectivity_term (id, year, member_id, collectivity_id, position_id, vote_id)
VALUES
    ('ct-col2-m1', 2026, 'C1-M1', 'col-2', 'pos-confirmed-member', 'vote-col2-2026'),
    ('ct-col2-m2', 2026, 'C1-M2', 'col-2', 'pos-confirmed-member', 'vote-col2-2026'),
    ('ct-col2-m3', 2026, 'C1-M3', 'col-2', 'pos-confirmed-member', 'vote-col2-2026'),
    ('ct-col2-m4', 2026, 'C1-M4', 'col-2', 'pos-confirmed-member', 'vote-col2-2026'),
    ('ct-col2-m5', 2026, 'C1-M5', 'col-2', 'pos-president',        'vote-col2-2026'),
    ('ct-col2-m6', 2026, 'C1-M6', 'col-2', 'pos-vice-president',   'vote-col2-2026'),
    ('ct-col2-m7', 2026, 'C1-M7', 'col-2', 'pos-secretary',        'vote-col2-2026'),
    ('ct-col2-m8', 2026, 'C1-M8', 'col-2', 'pos-treasurer',        'vote-col2-2026');

-- col-3 (Tableau 4)
INSERT INTO collectivity_term (id, year, member_id, collectivity_id, position_id, vote_id)
VALUES
    ('ct-col3-m1', 2026, 'C3-M1', 'col-3', 'pos-president',        'vote-col3-2026'),
    ('ct-col3-m2', 2026, 'C3-M2', 'col-3', 'pos-vice-president',   'vote-col3-2026'),
    ('ct-col3-m3', 2026, 'C3-M3', 'col-3', 'pos-secretary',        'vote-col3-2026'),
    ('ct-col3-m4', 2026, 'C3-M4', 'col-3', 'pos-treasurer',        'vote-col3-2026'),
    ('ct-col3-m5', 2026, 'C3-M5', 'col-3', 'pos-confirmed-member', 'vote-col3-2026'),
    ('ct-col3-m6', 2026, 'C3-M6', 'col-3', 'pos-confirmed-member', 'vote-col3-2026'),
    ('ct-col3-m7', 2026, 'C3-M7', 'col-3', 'pos-confirmed-member', 'vote-col3-2026'),
    ('ct-col3-m8', 2026, 'C3-M8', 'col-3', 'pos-confirmed-member', 'vote-col3-2026');

-- ============================================================
-- 9. COLLECTIVITY STRUCTURE (bureaux)
-- ============================================================
INSERT INTO collectivity_structure (id, collectivity_id, president_id, vice_president_id, treasurer_id, secretary_id)
VALUES
    ('cs-col-1', 'col-1', 'C1-M1', 'C1-M2', 'C1-M4', 'C1-M3'),
    ('cs-col-2', 'col-2', 'C1-M5', 'C1-M6', 'C1-M8', 'C1-M7'),
    ('cs-col-3', 'col-3', 'C3-M1', 'C3-M2', 'C3-M4', 'C3-M3');

-- ============================================================
-- 10. COMPTES FINANCIERS DE BASE (à réinsérer avant les nouveaux)
-- ============================================================

-- col-1 : caisse + Orange Money
INSERT INTO account (id, type, collectivity_id, federation_id, balance)
VALUES
    ('C1-A-CASH',     'cash',         'col-1', NULL, 0),
    ('C1-A-MOBILE-1', 'mobile_money', 'col-1', NULL, 0);

INSERT INTO account_mobile (account_id, holder_name, service_name, phone_number)
VALUES ('C1-A-MOBILE-1', 'Mpanorina', 'ORANGE_MONEY', '0370489612');

-- col-2 : caisse + Orange Money
INSERT INTO account (id, type, collectivity_id, federation_id, balance)
VALUES
    ('C2-A-CASH',     'cash',         'col-2', NULL, 0),
    ('C2-A-MOBILE-1', 'mobile_money', 'col-2', NULL, 0);

INSERT INTO account_mobile (account_id, holder_name, service_name, phone_number)
VALUES ('C2-A-MOBILE-1', 'Dobo voalohany', 'ORANGE_MONEY', '0320489612');

-- col-3 : caisse seulement (de base)
INSERT INTO account (id, type, collectivity_id, federation_id, balance)
VALUES ('C3-A-CASH', 'cash', 'col-3', NULL, 0);

-- ============================================================
-- NOUVELLES DONNÉES DU 6 MAI 2026 (pages 24-29)
-- ============================================================

-- ============================================================
-- 11. NOUVEAUX COMPTES col-3 (page 24 — point 1)
--     2 comptes bancaires + 1 compte mobile money
-- ============================================================

-- Comptes bancaires col-3
INSERT INTO account (id, type, collectivity_id, federation_id, balance)
VALUES
    ('C3-A-BANK-1', 'bank', 'col-3', NULL, 0),
    ('C3-A-BANK-2', 'bank', 'col-3', NULL, 0);

-- account_extended ajoute bank_code et branch_code (migration V0.0.3)
-- account_number stocke les 11 chiffres du numéro de compte (hors code banque/guichet/clé)
-- rib_key = clé RIB (2 chiffres)
INSERT INTO account_extended (account_id, holder_name, bank_name, account_number, rib_key, bank_code, branch_code)
VALUES
    ('C3-A-BANK-1', 'Koto',  'BMOI', '1234567890 ', '12', '00004', '00001'),
    ('C3-A-BANK-2', 'Naivo', 'BRED', '4567890123 ', '58', '00008', '00003');

-- Compte mobile money Mvola col-3
INSERT INTO account (id, type, collectivity_id, federation_id, balance)
VALUES ('C3-A-MOBILE-1', 'mobile_money', 'col-3', NULL, 0);

INSERT INTO account_mobile (account_id, holder_name, service_name, phone_number)
VALUES ('C3-A-MOBILE-1', 'Kolo', 'MVOLA', '0341889612');

-- ============================================================
-- 12. COTISATIONS (pages 25 — point 2)
--     Nouvelles listes remplaçant les anciennes
-- ============================================================

-- col-1 (Tableau 12)
INSERT INTO membership_fee (id, label, status, frequency, eligible_from, amount, collectivity_id)
VALUES
    ('cot-1', 'Cotisation annuelle', 'ACTIVE',   'ANNUALLY',   '2026-01-01', 200000.00, 'col-1'),
    ('cot-2', 'Famangiana',          'ACTIVE',   'PUNCTUALLY', '2026-04-30',  20000.00, 'col-1');

-- col-2 (Tableau 13)
INSERT INTO membership_fee (id, label, status, frequency, eligible_from, amount, collectivity_id)
VALUES
    ('cot-3', 'Cotisation annuelle', 'ACTIVE',   'ANNUALLY', '2026-01-01', 200000.00, 'col-2'),
    ('cot-4', 'Cotisation 2025',     'INACTIVE', 'ANNUALLY', '2025-01-01', 100000.00, 'col-2');

-- col-3 (Tableau 14)
INSERT INTO membership_fee (id, label, status, frequency, eligible_from, amount, collectivity_id)
VALUES
    ('cot-5', 'Cotisation mensuelle', 'ACTIVE', 'MONTHLY', '2026-04-01', 25000.00, 'col-3');

-- ============================================================
-- 13. PAIEMENTS (contributions) ET TRANSACTIONS — col-1 (Tableau 15, page 26)
--     Nouveaux montants selon les nouvelles cotisations
-- ============================================================

-- Contributions col-1
INSERT INTO contribution (id, amount, collection_date, payment_method, type, member_id,
                          collectivity_id, membership_fee_id, account_credited_id, creation_date, label)
VALUES
    ('con-c1-m1', 200000.00, '2026-01-01', 'CASH',           'ANNUALLY', 'C1-M1', 'col-1', 'cot-1', 'C1-A-CASH',     '2026-01-01', 'Cotisation annuelle 2026'),
    ('con-c1-m2', 200000.00, '2026-01-01', 'CASH',           'ANNUALLY', 'C1-M2', 'col-1', 'cot-1', 'C1-A-CASH',     '2026-01-01', 'Cotisation annuelle 2026'),
    ('con-c1-m3', 200000.00, '2026-01-01', 'MOBILE_BANKING', 'ANNUALLY', 'C1-M3', 'col-1', 'cot-1', 'C1-A-MOBILE-1', '2026-01-01', 'Cotisation annuelle 2026'),
    ('con-c1-m4', 200000.00, '2026-01-01', 'MOBILE_BANKING', 'ANNUALLY', 'C1-M4', 'col-1', 'cot-1', 'C1-A-MOBILE-1', '2026-01-01', 'Cotisation annuelle 2026'),
    ('con-c1-m5', 150000.00, '2026-01-01', 'MOBILE_BANKING', 'ANNUALLY', 'C1-M5', 'col-1', 'cot-1', 'C1-A-MOBILE-1', '2026-01-01', 'Cotisation annuelle 2026 (partielle)'),
    ('con-c1-m6', 100000.00, '2026-05-01', 'CASH',           'ANNUALLY', 'C1-M6', 'col-1', 'cot-1', 'C1-A-CASH',     '2026-05-01', 'Cotisation annuelle 2026 (partielle)'),
    ('con-c1-m7',  60000.00, '2026-05-01', 'CASH',           'ANNUALLY', 'C1-M7', 'col-1', 'cot-1', 'C1-A-CASH',     '2026-05-01', 'Cotisation annuelle 2026 (partielle)'),
    ('con-c1-m8',  90000.00, '2026-05-01', 'CASH',           'ANNUALLY', 'C1-M8', 'col-1', 'cot-1', 'C1-A-CASH',     '2026-05-01', 'Cotisation annuelle 2026 (partielle)');

-- Transactions col-1
INSERT INTO "transaction" (id, account_id, amount, transaction_date, description, member_id)
VALUES
    ('tr-c1-m1', 'C1-A-CASH',     200000.00, '2026-01-01', 'Cotisation annuelle 2026',             'C1-M1'),
    ('tr-c1-m2', 'C1-A-CASH',     200000.00, '2026-01-01', 'Cotisation annuelle 2026',             'C1-M2'),
    ('tr-c1-m3', 'C1-A-MOBILE-1', 200000.00, '2026-01-01', 'Cotisation annuelle 2026',             'C1-M3'),
    ('tr-c1-m4', 'C1-A-MOBILE-1', 200000.00, '2026-01-01', 'Cotisation annuelle 2026',             'C1-M4'),
    ('tr-c1-m5', 'C1-A-MOBILE-1', 150000.00, '2026-01-01', 'Cotisation annuelle 2026 (partielle)', 'C1-M5'),
    ('tr-c1-m6', 'C1-A-CASH',     100000.00, '2026-05-01', 'Cotisation annuelle 2026 (partielle)', 'C1-M6'),
    ('tr-c1-m7', 'C1-A-CASH',      60000.00, '2026-05-01', 'Cotisation annuelle 2026 (partielle)', 'C1-M7'),
    ('tr-c1-m8', 'C1-A-CASH',      90000.00, '2026-05-01', 'Cotisation annuelle 2026 (partielle)', 'C1-M8');

-- ============================================================
-- 14. PAIEMENTS ET TRANSACTIONS — col-2 (Tableau 16, page 26)
-- ============================================================

-- Contributions col-2
INSERT INTO contribution (id, amount, collection_date, payment_method, type, member_id,
                          collectivity_id, membership_fee_id, account_credited_id, creation_date, label)
VALUES
    ('con-c2-m1', 120000.00, '2026-01-01', 'CASH',           'ANNUALLY', 'C1-M1', 'col-2', 'cot-3', 'C2-A-CASH',     '2026-01-01', 'Cotisation annuelle 2026 (partielle)'),
    ('con-c2-m2', 180000.00, '2026-01-01', 'CASH',           'ANNUALLY', 'C1-M2', 'col-2', 'cot-3', 'C2-A-CASH',     '2026-01-01', 'Cotisation annuelle 2026 (partielle)'),
    ('con-c2-m3', 200000.00, '2026-01-01', 'CASH',           'ANNUALLY', 'C1-M3', 'col-2', 'cot-3', 'C2-A-CASH',     '2026-01-01', 'Cotisation annuelle 2026'),
    ('con-c2-m4', 200000.00, '2026-01-01', 'CASH',           'ANNUALLY', 'C1-M4', 'col-2', 'cot-3', 'C2-A-CASH',     '2026-01-01', 'Cotisation annuelle 2026'),
    ('con-c2-m5', 200000.00, '2026-01-01', 'CASH',           'ANNUALLY', 'C1-M5', 'col-2', 'cot-3', 'C2-A-CASH',     '2026-01-01', 'Cotisation annuelle 2026'),
    ('con-c2-m6', 200000.00, '2026-01-01', 'CASH',           'ANNUALLY', 'C1-M6', 'col-2', 'cot-3', 'C2-A-CASH',     '2026-01-01', 'Cotisation annuelle 2026'),
    ('con-c2-m7',  80000.00, '2026-01-01', 'MOBILE_BANKING', 'ANNUALLY', 'C1-M7', 'col-2', 'cot-3', 'C2-A-MOBILE-1', '2026-01-01', 'Cotisation annuelle 2026 (partielle)'),
    ('con-c2-m8', 120000.00, '2026-01-01', 'MOBILE_BANKING', 'ANNUALLY', 'C1-M8', 'col-2', 'cot-3', 'C2-A-MOBILE-1', '2026-01-01', 'Cotisation annuelle 2026 (partielle)');

-- Transactions col-2
INSERT INTO "transaction" (id, account_id, amount, transaction_date, description, member_id)
VALUES
    ('tr-c2-m1', 'C2-A-CASH',     120000.00, '2026-01-01', 'Cotisation annuelle 2026 (partielle)', 'C1-M1'),
    ('tr-c2-m2', 'C2-A-CASH',     180000.00, '2026-01-01', 'Cotisation annuelle 2026 (partielle)', 'C1-M2'),
    ('tr-c2-m3', 'C2-A-CASH',     200000.00, '2026-01-01', 'Cotisation annuelle 2026',             'C1-M3'),
    ('tr-c2-m4', 'C2-A-CASH',     200000.00, '2026-01-01', 'Cotisation annuelle 2026',             'C1-M4'),
    ('tr-c2-m5', 'C2-A-CASH',     200000.00, '2026-01-01', 'Cotisation annuelle 2026',             'C1-M5'),
    ('tr-c2-m6', 'C2-A-CASH',     200000.00, '2026-01-01', 'Cotisation annuelle 2026',             'C1-M6'),
    ('tr-c2-m7', 'C2-A-MOBILE-1',  80000.00, '2026-01-01', 'Cotisation annuelle 2026 (partielle)', 'C1-M7'),
    ('tr-c2-m8', 'C2-A-MOBILE-1', 120000.00, '2026-01-01', 'Cotisation annuelle 2026 (partielle)', 'C1-M8');

-- ============================================================
-- 15. PAIEMENTS ET TRANSACTIONS — col-3 (Tableau 17, page 27)
--     Paiements en avril et mai 2026
-- ============================================================

-- Contributions col-3
INSERT INTO contribution (id, amount, collection_date, payment_method, type, member_id,
                          collectivity_id, membership_fee_id, account_credited_id, creation_date, label)
VALUES
    -- Avril 2026
    ('con-c3-m1-apr', 25000.00, '2026-04-01', 'BANK_TRANSFER', 'MONTHLY', 'C3-M1', 'col-3', 'cot-5', 'C3-A-BANK-1', '2026-04-01', 'Cotisation mensuelle avril 2026'),
    ('con-c3-m2-apr', 25000.00, '2026-04-01', 'BANK_TRANSFER', 'MONTHLY', 'C3-M2', 'col-3', 'cot-5', 'C3-A-BANK-1', '2026-04-01', 'Cotisation mensuelle avril 2026'),
    ('con-c3-m3-apr', 25000.00, '2026-04-01', 'BANK_TRANSFER', 'MONTHLY', 'C3-M3', 'col-3', 'cot-5', 'C3-A-BANK-1', '2026-04-01', 'Cotisation mensuelle avril 2026'),
    ('con-c3-m4-apr', 25000.00, '2026-04-01', 'BANK_TRANSFER', 'MONTHLY', 'C3-M4', 'col-3', 'cot-5', 'C3-A-BANK-1', '2026-04-01', 'Cotisation mensuelle avril 2026'),
    ('con-c3-m5-apr', 25000.00, '2026-04-01', 'BANK_TRANSFER', 'MONTHLY', 'C3-M5', 'col-3', 'cot-5', 'C3-A-BANK-2', '2026-04-01', 'Cotisation mensuelle avril 2026'),
    ('con-c3-m6-apr', 25000.00, '2026-04-01', 'BANK_TRANSFER', 'MONTHLY', 'C3-M6', 'col-3', 'cot-5', 'C3-A-BANK-2', '2026-04-01', 'Cotisation mensuelle avril 2026'),
    ('con-c3-m7-apr', 25000.00, '2026-04-01', 'CASH',          'MONTHLY', 'C3-M7', 'col-3', 'cot-5', 'C3-A-CASH',   '2026-04-01', 'Cotisation mensuelle avril 2026'),
    ('con-c3-m8-apr', 25000.00, '2026-04-01', 'CASH',          'MONTHLY', 'C3-M8', 'col-3', 'cot-5', 'C3-A-CASH',   '2026-04-01', 'Cotisation mensuelle avril 2026'),
    -- Mai 2026
    ('con-c3-m1-may', 25000.00, '2026-05-01', 'BANK_TRANSFER', 'MONTHLY', 'C3-M1', 'col-3', 'cot-5', 'C3-A-BANK-1',   '2026-05-01', 'Cotisation mensuelle mai 2026'),
    ('con-c3-m2-may', 25000.00, '2026-05-01', 'BANK_TRANSFER', 'MONTHLY', 'C3-M2', 'col-3', 'cot-5', 'C3-A-BANK-1',   '2026-05-01', 'Cotisation mensuelle mai 2026'),
    ('con-c3-m3-may', 15000.00, '2026-05-01', 'MOBILE_BANKING','MONTHLY', 'C3-M3', 'col-3', 'cot-5', 'C3-A-MOBILE-1', '2026-05-01', 'Cotisation mensuelle mai 2026 (partielle)'),
    ('con-c3-m4-may', 15000.00, '2026-05-01', 'MOBILE_BANKING','MONTHLY', 'C3-M4', 'col-3', 'cot-5', 'C3-A-MOBILE-1', '2026-05-01', 'Cotisation mensuelle mai 2026 (partielle)'),
    ('con-c3-m5-may', 20000.00, '2026-05-01', 'BANK_TRANSFER', 'MONTHLY', 'C3-M5', 'col-3', 'cot-5', 'C3-A-BANK-2',   '2026-05-01', 'Cotisation mensuelle mai 2026 (partielle)'),
    ('con-c3-m6-may', 25000.00, '2026-05-01', 'BANK_TRANSFER', 'MONTHLY', 'C3-M6', 'col-3', 'cot-5', 'C3-A-BANK-2',   '2026-05-01', 'Cotisation mensuelle mai 2026'),
    ('con-c3-m7-may',  5000.00, '2026-05-01', 'CASH',          'MONTHLY', 'C3-M7', 'col-3', 'cot-5', 'C3-A-CASH',     '2026-05-01', 'Cotisation mensuelle mai 2026 (partielle)'),
    ('con-c3-m8-may',  5000.00, '2026-05-01', 'CASH',          'MONTHLY', 'C3-M8', 'col-3', 'cot-5', 'C3-A-CASH',     '2026-05-01', 'Cotisation mensuelle mai 2026 (partielle)');

-- Transactions col-3
INSERT INTO "transaction" (id, account_id, amount, transaction_date, description, member_id)
VALUES
    -- Avril 2026
    ('tr-c3-m1-apr', 'C3-A-BANK-1',   25000.00, '2026-04-01', 'Cotisation mensuelle avril 2026', 'C3-M1'),
    ('tr-c3-m2-apr', 'C3-A-BANK-1',   25000.00, '2026-04-01', 'Cotisation mensuelle avril 2026', 'C3-M2'),
    ('tr-c3-m3-apr', 'C3-A-BANK-1',   25000.00, '2026-04-01', 'Cotisation mensuelle avril 2026', 'C3-M3'),
    ('tr-c3-m4-apr', 'C3-A-BANK-1',   25000.00, '2026-04-01', 'Cotisation mensuelle avril 2026', 'C3-M4'),
    ('tr-c3-m5-apr', 'C3-A-BANK-2',   25000.00, '2026-04-01', 'Cotisation mensuelle avril 2026', 'C3-M5'),
    ('tr-c3-m6-apr', 'C3-A-BANK-2',   25000.00, '2026-04-01', 'Cotisation mensuelle avril 2026', 'C3-M6'),
    ('tr-c3-m7-apr', 'C3-A-CASH',     25000.00, '2026-04-01', 'Cotisation mensuelle avril 2026', 'C3-M7'),
    ('tr-c3-m8-apr', 'C3-A-CASH',     25000.00, '2026-04-01', 'Cotisation mensuelle avril 2026', 'C3-M8'),
    -- Mai 2026
    ('tr-c3-m1-may', 'C3-A-BANK-1',   25000.00, '2026-05-01', 'Cotisation mensuelle mai 2026',             'C3-M1'),
    ('tr-c3-m2-may', 'C3-A-BANK-1',   25000.00, '2026-05-01', 'Cotisation mensuelle mai 2026',             'C3-M2'),
    ('tr-c3-m3-may', 'C3-A-MOBILE-1', 15000.00, '2026-05-01', 'Cotisation mensuelle mai 2026 (partielle)', 'C3-M3'),
    ('tr-c3-m4-may', 'C3-A-MOBILE-1', 15000.00, '2026-05-01', 'Cotisation mensuelle mai 2026 (partielle)', 'C3-M4'),
    ('tr-c3-m5-may', 'C3-A-BANK-2',   20000.00, '2026-05-01', 'Cotisation mensuelle mai 2026 (partielle)', 'C3-M5'),
    ('tr-c3-m6-may', 'C3-A-BANK-2',   25000.00, '2026-05-01', 'Cotisation mensuelle mai 2026',             'C3-M6'),
    ('tr-c3-m7-may', 'C3-A-CASH',      5000.00, '2026-05-01', 'Cotisation mensuelle mai 2026 (partielle)', 'C3-M7'),
    ('tr-c3-m8-may', 'C3-A-CASH',      5000.00, '2026-05-01', 'Cotisation mensuelle mai 2026 (partielle)', 'C3-M8');

-- ============================================================
-- 16. MISE À JOUR DES SOLDES DES COMPTES
-- col-1 :
--   cash     : 200k+200k + 100k+60k+90k       = 650 000
--   mobile-1 : 200k+200k+150k                 = 550 000
-- col-2 :
--   cash     : 120k+180k+200k+200k+200k+200k  = 1 100 000
--   mobile-1 : 80k+120k                       = 200 000
-- col-3 :
--   bank-1   : 25k*4(avr) + 25k+25k(mai)      = 150 000
--   bank-2   : 25k+25k(avr) + 20k+25k(mai)    = 95 000
--   mobile-1 : 15k+15k(mai)                   = 30 000
--   cash     : 25k+25k(avr) + 5k+5k(mai)      = 60 000
-- ============================================================
UPDATE account SET balance =  650000.00 WHERE id = 'C1-A-CASH';
UPDATE account SET balance =  550000.00 WHERE id = 'C1-A-MOBILE-1';
UPDATE account SET balance = 1100000.00 WHERE id = 'C2-A-CASH';
UPDATE account SET balance =  200000.00 WHERE id = 'C2-A-MOBILE-1';
UPDATE account SET balance =  150000.00 WHERE id = 'C3-A-BANK-1';
UPDATE account SET balance =   95000.00 WHERE id = 'C3-A-BANK-2';
UPDATE account SET balance =   30000.00 WHERE id = 'C3-A-MOBILE-1';
UPDATE account SET balance =   60000.00 WHERE id = 'C3-A-CASH';

-- ============================================================
-- 17. NOUVEAUX MEMBRES JUNIORS (pages 27-29, Tableaux 18-20)
--     Données <random> → valeurs fictives cohérentes
--     Tous parrainés par 2 membres confirmés de la collectivité cible
-- ============================================================

-- --- Nouveaux membres (entités physiques) ---

-- col-1 : 4 nouveaux juniors (Tableau 18)
INSERT INTO member (id, lastname, firstname, birth_date, gender, address, occupation,
                    phone, email, membership_date,
                    registration_fee_paid, membership_dues_paid, membership_type)
VALUES
    ('C1-NM1', 'Nouveau membre 1',  'Prénom NM1',  '2000-03-15', 'MALE',   'Lot NM1 Ambato.',  'Agriculteur', '0340000001', 'nm1.col1@fed-agri.mg',  '2026-04-01', TRUE, FALSE, 'JUNIOR'),
    ('C1-NM2', 'Nouveau membre 2',  'Prénom NM2',  '2001-07-20', 'FEMALE', 'Lot NM2 Ambato.',  'Agriculteur', '0340000002', 'nm2.col1@fed-agri.mg',  '2026-04-01', TRUE, FALSE, 'JUNIOR'),
    ('C1-NM3', 'Nouveau membre 3',  'Prénom NM3',  '1999-11-05', 'MALE',   'Lot NM3 Ambato.',  'Agriculteur', '0340000003', 'nm3.col1@fed-agri.mg',  '2026-05-01', TRUE, FALSE, 'JUNIOR'),
    ('C1-NM4', 'Nouveau membre 4',  'Prénom NM4',  '2002-01-10', 'MALE',   'Lot NM4 Ambato.',  'Agriculteur', '0340000004', 'nm4.col1@fed-agri.mg',  '2026-06-01', TRUE, FALSE, 'JUNIOR');

-- col-2 : 3 nouveaux juniors (Tableau 19)
INSERT INTO member (id, lastname, firstname, birth_date, gender, address, occupation,
                    phone, email, membership_date,
                    registration_fee_paid, membership_dues_paid, membership_type)
VALUES
    ('C2-NM1', 'Nouveau membre 5',  'Prénom NM5',  '2000-06-12', 'FEMALE', 'Lot NM5 Ambato.',  'Agriculteur', '0340000005', 'nm5.col2@fed-agri.mg',  '2026-03-01', TRUE, FALSE, 'JUNIOR'),
    ('C2-NM2', 'Nouveau membre 6',  'Prénom NM6',  '2001-09-25', 'MALE',   'Lot NM6 Ambato.',  'Agriculteur', '0340000006', 'nm6.col2@fed-agri.mg',  '2026-03-01', TRUE, FALSE, 'JUNIOR'),
    ('C2-NM3', 'Nouveau membre 7',  'Prénom NM7',  '1998-04-18', 'MALE',   'Lot NM7 Ambato.',  'Agriculteur', '0340000007', 'nm7.col2@fed-agri.mg',  '2026-03-01', TRUE, FALSE, 'JUNIOR');

-- col-3 : 6 nouveaux juniors (Tableau 20)
INSERT INTO member (id, lastname, firstname, birth_date, gender, address, occupation,
                    phone, email, membership_date,
                    registration_fee_paid, membership_dues_paid, membership_type)
VALUES
    ('C3-NM1', 'Nouveau membre 8',  'Prénom NM8',  '2000-05-03', 'MALE',   'Lot NM8 Antsirabe.',  'Apiculteur', '0340000008', 'nm8.col3@fed-agri.mg',  '2026-01-01', TRUE, FALSE, 'JUNIOR'),
    ('C3-NM2', 'Nouveau membre 9',  'Prénom NM9',  '2001-12-14', 'FEMALE', 'Lot NM9 Antsirabe.',  'Apiculteur', '0340000009', 'nm9.col3@fed-agri.mg',  '2026-02-01', TRUE, FALSE, 'JUNIOR'),
    ('C3-NM3', 'Nouveau membre 10', 'Prénom NM10', '1999-08-29', 'MALE',   'Lot NM10 Antsirabe.', 'Apiculteur', '0340000010', 'nm10.col3@fed-agri.mg', '2026-02-01', TRUE, FALSE, 'JUNIOR'),
    ('C3-NM4', 'Nouveau membre 11', 'Prénom NM11', '2002-02-22', 'MALE',   'Lot NM11 Antsirabe.', 'Apiculteur', '0340000011', 'nm11.col3@fed-agri.mg', '2026-03-01', TRUE, FALSE, 'JUNIOR'),
    ('C3-NM5', 'Nouveau membre 12', 'Prénom NM12', '2000-07-09', 'FEMALE', 'Lot NM12 Antsirabe.', 'Apiculteur', '0340000012', 'nm12.col3@fed-agri.mg', '2026-03-01', TRUE, FALSE, 'JUNIOR'),
    ('C3-NM6', 'Nouveau membre 13', 'Prénom NM13', '2001-03-31', 'MALE',   'Lot NM13 Antsirabe.', 'Apiculteur', '0340000013', 'nm13.col3@fed-agri.mg', '2026-03-01', TRUE, FALSE, 'JUNIOR');

-- --- Membership history pour les nouveaux membres ---

-- col-1
INSERT INTO membership_history (id, start_date, end_date, reason, member_id, collectivity_id)
VALUES
    ('mh-c1-nm1', '2026-04-01', NULL, 'ADMISSION', 'C1-NM1', 'col-1'),
    ('mh-c1-nm2', '2026-04-01', NULL, 'ADMISSION', 'C1-NM2', 'col-1'),
    ('mh-c1-nm3', '2026-05-01', NULL, 'ADMISSION', 'C1-NM3', 'col-1'),
    ('mh-c1-nm4', '2026-06-01', NULL, 'ADMISSION', 'C1-NM4', 'col-1');

-- col-2
INSERT INTO membership_history (id, start_date, end_date, reason, member_id, collectivity_id)
VALUES
    ('mh-c2-nm1', '2026-03-01', NULL, 'ADMISSION', 'C2-NM1', 'col-2'),
    ('mh-c2-nm2', '2026-03-01', NULL, 'ADMISSION', 'C2-NM2', 'col-2'),
    ('mh-c2-nm3', '2026-03-01', NULL, 'ADMISSION', 'C2-NM3', 'col-2');

-- col-3
INSERT INTO membership_history (id, start_date, end_date, reason, member_id, collectivity_id)
VALUES
    ('mh-c3-nm1', '2026-01-01', NULL, 'ADMISSION', 'C3-NM1', 'col-3'),
    ('mh-c3-nm2', '2026-02-01', NULL, 'ADMISSION', 'C3-NM2', 'col-3'),
    ('mh-c3-nm3', '2026-02-01', NULL, 'ADMISSION', 'C3-NM3', 'col-3'),
    ('mh-c3-nm4', '2026-03-01', NULL, 'ADMISSION', 'C3-NM4', 'col-3'),
    ('mh-c3-nm5', '2026-03-01', NULL, 'ADMISSION', 'C3-NM5', 'col-3'),
    ('mh-c3-nm6', '2026-03-01', NULL, 'ADMISSION', 'C3-NM6', 'col-3');

-- --- Collectivity terms (JUNIOR_MEMBER) pour les nouveaux membres ---

-- col-1
INSERT INTO collectivity_term (id, year, member_id, collectivity_id, position_id, vote_id)
VALUES
    ('ct-c1-nm1', 2026, 'C1-NM1', 'col-1', 'pos-junior-member', 'vote-col1-2026'),
    ('ct-c1-nm2', 2026, 'C1-NM2', 'col-1', 'pos-junior-member', 'vote-col1-2026'),
    ('ct-c1-nm3', 2026, 'C1-NM3', 'col-1', 'pos-junior-member', 'vote-col1-2026'),
    ('ct-c1-nm4', 2026, 'C1-NM4', 'col-1', 'pos-junior-member', 'vote-col1-2026');

-- col-2
INSERT INTO collectivity_term (id, year, member_id, collectivity_id, position_id, vote_id)
VALUES
    ('ct-c2-nm1', 2026, 'C2-NM1', 'col-2', 'pos-junior-member', 'vote-col2-2026'),
    ('ct-c2-nm2', 2026, 'C2-NM2', 'col-2', 'pos-junior-member', 'vote-col2-2026'),
    ('ct-c2-nm3', 2026, 'C2-NM3', 'col-2', 'pos-junior-member', 'vote-col2-2026');

-- col-3
INSERT INTO collectivity_term (id, year, member_id, collectivity_id, position_id, vote_id)
VALUES
    ('ct-c3-nm1', 2026, 'C3-NM1', 'col-3', 'pos-junior-member', 'vote-col3-2026'),
    ('ct-c3-nm2', 2026, 'C3-NM2', 'col-3', 'pos-junior-member', 'vote-col3-2026'),
    ('ct-c3-nm3', 2026, 'C3-NM3', 'col-3', 'pos-junior-member', 'vote-col3-2026'),
    ('ct-c3-nm4', 2026, 'C3-NM4', 'col-3', 'pos-junior-member', 'vote-col3-2026'),
    ('ct-c3-nm5', 2026, 'C3-NM5', 'col-3', 'pos-junior-member', 'vote-col3-2026'),
    ('ct-c3-nm6', 2026, 'C3-NM6', 'col-3', 'pos-junior-member', 'vote-col3-2026');

-- --- Sponsorships pour les nouveaux membres (parrains de la collectivité cible) ---

-- col-1 nouveaux (Tableau 18) — parrains : C1-M1 et C1-M2
INSERT INTO sponsorship (id, sponsorship_date, sponsor_member_id, sponsored_member_id, relationship)
VALUES
    ('sp-c1nm1-m1', '2026-04-01', 'C1-M1', 'C1-NM1', 'amis'),
    ('sp-c1nm1-m2', '2026-04-01', 'C1-M2', 'C1-NM1', 'amis'),
    ('sp-c1nm2-m1', '2026-04-01', 'C1-M1', 'C1-NM2', 'amis'),
    ('sp-c1nm2-m2', '2026-04-01', 'C1-M2', 'C1-NM2', 'amis'),
    ('sp-c1nm3-m1', '2026-05-01', 'C1-M1', 'C1-NM3', 'amis'),
    ('sp-c1nm3-m2', '2026-05-01', 'C1-M2', 'C1-NM3', 'amis'),
    ('sp-c1nm4-m1', '2026-06-01', 'C1-M1', 'C1-NM4', 'amis'),
    ('sp-c1nm4-m2', '2026-06-01', 'C1-M2', 'C1-NM4', 'amis');

-- col-2 nouveaux (Tableau 19) — parrains : C1-M1 et C1-M2 (membres confirmés de col-2)
INSERT INTO sponsorship (id, sponsorship_date, sponsor_member_id, sponsored_member_id, relationship)
VALUES
    ('sp-c2nm1-m1', '2026-03-01', 'C1-M1', 'C2-NM1', 'famille'),
    ('sp-c2nm1-m2', '2026-03-01', 'C1-M2', 'C2-NM1', 'famille'),
    ('sp-c2nm2-m1', '2026-03-01', 'C1-M1', 'C2-NM2', 'famille'),
    ('sp-c2nm2-m2', '2026-03-01', 'C1-M2', 'C2-NM2', 'famille'),
    ('sp-c2nm3-m1', '2026-03-01', 'C1-M1', 'C2-NM3', 'collègues'),
    ('sp-c2nm3-m2', '2026-03-01', 'C1-M2', 'C2-NM3', 'collègues');

-- col-3 nouveaux (Tableau 20) — parrains : C3-M1 et C3-M2
INSERT INTO sponsorship (id, sponsorship_date, sponsor_member_id, sponsored_member_id, relationship)
VALUES
    ('sp-c3nm1-m1', '2026-01-01', 'C3-M1', 'C3-NM1', 'famille'),
    ('sp-c3nm1-m2', '2026-01-01', 'C3-M2', 'C3-NM1', 'famille'),
    ('sp-c3nm2-m1', '2026-02-01', 'C3-M1', 'C3-NM2', 'amis'),
    ('sp-c3nm2-m2', '2026-02-01', 'C3-M2', 'C3-NM2', 'amis'),
    ('sp-c3nm3-m1', '2026-02-01', 'C3-M1', 'C3-NM3', 'collègues'),
    ('sp-c3nm3-m2', '2026-02-01', 'C3-M2', 'C3-NM3', 'collègues'),
    ('sp-c3nm4-m1', '2026-03-01', 'C3-M1', 'C3-NM4', 'famille'),
    ('sp-c3nm4-m2', '2026-03-01', 'C3-M2', 'C3-NM4', 'famille'),
    ('sp-c3nm5-m1', '2026-03-01', 'C3-M1', 'C3-NM5', 'amis'),
    ('sp-c3nm5-m2', '2026-03-01', 'C3-M2', 'C3-NM5', 'amis'),
    ('sp-c3nm6-m1', '2026-03-01', 'C3-M1', 'C3-NM6', 'collègues'),
    ('sp-c3nm6-m2', '2026-03-01', 'C3-M2', 'C3-NM6', 'collègues');

-- ============================================================
-- END OF SEED — 6 MAI 2026
-- ============================================================
