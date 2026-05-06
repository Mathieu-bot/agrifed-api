-- ============================================================
-- Seed: Données de test - TD Final 23 Avril 2026
-- Source: PDF pages 11-20
-- Basé sur: db_init_v0.sql + migrations V0.0.1, V0.0.3, V0.0.4
-- ============================================================

-- ============================================================
-- 1. FEDERATION
-- ============================================================
INSERT INTO federation (id, name)
VALUES ('fed-1', 'Fédération de collectivités agricoles')
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 2. COLLECTIVITES (Tableau 1, page 11)
-- Colonnes: id, number, name, specialty, city, creation_date,
--           federation_id, status, location (v0.0.1), federation_approval (v0.0.1)
-- ============================================================
INSERT INTO collectivity (id, number, name, specialty, city, creation_date, federation_id, status, location, federation_approval)
VALUES
    ('col-1', '1', 'Mpanorina',      'Riziculture', 'Ambatondrazaka', '2020-01-01', 'fed-1', 'APPROVED', 'Ambatondrazaka', TRUE),
    ('col-2', '2', 'Dobo voalohany', 'Pisciculture', 'Ambatondrazaka', '2020-01-01', 'fed-1', 'APPROVED', 'Ambatondrazaka', TRUE),
    ('col-3', '3', 'Tantely mamy',   'Apiculture',   'Brickaville',   '2020-01-01', 'fed-1', 'APPROVED', 'Brickaville',   TRUE)
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 3. POSITIONS (référentiel complet)
-- label CHECK: PRESIDENT, VICE_PRESIDENT, TREASURER, SECRETARY, CONFIRMED_MEMBER, JUNIOR_MEMBER
-- context CHECK: BOTH, COLLECTIVITY, FEDERATION
-- ============================================================
INSERT INTO position (id, label, context)
VALUES
    ('pos-president',        'PRESIDENT',        'BOTH'),
    ('pos-vice-president',   'VICE_PRESIDENT',   'BOTH'),
    ('pos-treasurer',        'TREASURER',        'BOTH'),
    ('pos-secretary',        'SECRETARY',        'BOTH'),
    ('pos-confirmed-member', 'CONFIRMED_MEMBER', 'COLLECTIVITY'),
    ('pos-junior-member',    'JUNIOR_MEMBER',    'COLLECTIVITY')
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 4. MEMBRES
-- gender CHECK: 'MALE' | 'FEMALE'
-- registration_fee_paid, membership_dues_paid ajoutés par migration V0.0.1
-- Les membres C1-M1 à C1-M8 sont PARTAGÉS entre col-1 et col-2 (même entité physique)
-- membership_date = 2025-01-01 (> 6 mois avant la date du test 2026-04-23)
-- ============================================================

-- Membres partagés collectivité 1 & 2 (Tableaux 2 & 3)
INSERT INTO member (id, lastname, firstname, birth_date, gender, address, occupation, phone, email, membership_date, registration_fee_paid, membership_dues_paid)
VALUES
    ('C1-M1', 'Nom membre 1',  'Prénom membre 1',  '1980-02-01', 'MALE',   'Lot II V M Ambato.',  'Riziculteur',  '0341234567', 'member.1@fed-agri.mg',  '2025-01-01', TRUE, TRUE),
    ('C1-M2', 'Nom membre 2',  'Prénom membre 2',  '1982-03-05', 'MALE',   'Lot II F Ambato.',    'Agriculteur',  '0321234567', 'member.2@fed-agri.mg',  '2025-01-01', TRUE, TRUE),
    ('C1-M3', 'Nom membre 3',  'Prénom membre 3',  '1992-03-10', 'MALE',   'Lot II J Ambato.',    'Collecteur',   '0331234567', 'member.3@fed-agri.mg',  '2025-01-01', TRUE, TRUE),
    ('C1-M4', 'Nom membre 4',  'Prénom membre 4',  '1988-05-22', 'FEMALE', 'Lot A K 50 Ambato.',  'Distributeur', '0381234567', 'member.4@fed-agri.mg',  '2025-01-01', TRUE, TRUE),
    ('C1-M5', 'Nom membre 5',  'Prénom membre 5',  '1999-08-21', 'MALE',   'Lot UV 80 Ambato.',   'Riziculteur',  '0373434567', 'member.5@fed-agri.mg',  '2025-01-01', TRUE, TRUE),
    ('C1-M6', 'Nom membre 6',  'Prénom membre 6',  '1998-08-22', 'FEMALE', 'Lot UV 6 Ambato.',    'Riziculteur',  '0372234567', 'member.6@fed-agri.mg',  '2025-01-01', TRUE, TRUE),
    ('C1-M7', 'Nom membre 7',  'Prénom membre 7',  '1998-01-31', 'MALE',   'Lot UV 7 Ambato.',    'Riziculteur',  '0374234567', 'member.7@fed-agri.mg',  '2025-01-01', TRUE, FALSE),
    ('C1-M8', 'Nom membre 8',  'Prénom membre 8',  '1975-08-20', 'MALE',   'Lot UV 8 Ambato.',    'Riziculteur',  '0370234567', 'member.8@fed-agri.mg',  '2025-01-01', TRUE, FALSE)
ON CONFLICT (id) DO NOTHING;

-- Membres collectivité 3 (Tableau 4)
INSERT INTO member (id, lastname, firstname, birth_date, gender, address, occupation, phone, email, membership_date, registration_fee_paid, membership_dues_paid)
VALUES
    ('C3-M1', 'Nom membre 9',  'Prénom membre 9',  '1988-01-02', 'MALE',   'Lot 33 J Antsirabe',   'Apiculteur',   '034034567',  'member.9@fed-agri.mg',  '2025-01-01', TRUE, TRUE),
    ('C3-M2', 'Nom membre 10', 'Prénom membre 10', '1982-03-05', 'MALE',   'Lot 2 J Antsirabe',    'Agriculteur',  '0338634567', 'member.10@fed-agri.mg', '2025-01-01', TRUE, TRUE),
    ('C3-M3', 'Nom membre 11', 'Prénom membre 11', '1992-03-12', 'MALE',   'Lot 8 KM Antsirabe',   'Collecteur',   '0338234567', 'member.11@fed-agri.mg', '2025-01-01', TRUE, TRUE),
    ('C3-M4', 'Nom membre 12', 'Prénom membre 12', '1988-05-10', 'FEMALE', 'Lot A K 50 Antsirabe', 'Distributeur', '0382334567', 'member.12@fed-agri.mg', '2025-01-01', TRUE, TRUE),
    ('C3-M5', 'Nom membre 13', 'Prénom membre 13', '1999-08-11', 'MALE',   'Lot UV 80 Antsirabe',  'Apiculteur',   '0373365567', 'member.13@fed-agri.mg', '2025-01-01', TRUE, TRUE),
    ('C3-M6', 'Nom membre 14', 'Prénom membre 14', '1998-08-09', 'FEMALE', 'Lot UV 6 Antsirabe',   'Apiculteur',   '0378234567', 'member.14@fed-agri.mg', '2025-01-01', TRUE, TRUE),
    ('C3-M7', 'Nom membre 15', 'Prénom membre 15', '1998-01-13', 'MALE',   'Lot UV 7 Antsirabe',   'Apiculteur',   '0374914567', 'member.15@fed-agri.mg', '2025-01-01', TRUE, TRUE),
    ('C3-M8', 'Nom membre 16', 'Prénom membre 16', '1975-08-02', 'MALE',   'Lot UV 8 Antsirabe',   'Apiculteur',   '0370634567', 'member.16@fed-agri.mg', '2025-01-01', TRUE, TRUE)
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 5. SPONSORSHIPS
-- Colonnes réelles: id, sponsorship_date, sponsor_member_id, sponsored_member_id
-- Pas de colonne "relationship"
-- C1-M1 et C1-M2 n'ont aucun parrain (fondateurs)
-- ============================================================

-- Parrainages col-1 (Tableau 2)
INSERT INTO sponsorship (id, sponsorship_date, sponsor_member_id, sponsored_member_id)
VALUES
    ('sp-c1m3-m1', '2025-01-01', 'C1-M1', 'C1-M3'),
    ('sp-c1m3-m2', '2025-01-01', 'C1-M2', 'C1-M3'),
    ('sp-c1m4-m1', '2025-01-01', 'C1-M1', 'C1-M4'),
    ('sp-c1m4-m2', '2025-01-01', 'C1-M2', 'C1-M4'),
    ('sp-c1m5-m1', '2025-01-01', 'C1-M1', 'C1-M5'),
    ('sp-c1m5-m2', '2025-01-01', 'C1-M2', 'C1-M5'),
    ('sp-c1m6-m1', '2025-01-01', 'C1-M1', 'C1-M6'),
    ('sp-c1m6-m2', '2025-01-01', 'C1-M2', 'C1-M6'),
    ('sp-c1m7-m1', '2025-01-01', 'C1-M1', 'C1-M7'),
    ('sp-c1m7-m2', '2025-01-01', 'C1-M2', 'C1-M7'),
    ('sp-c1m8-m6', '2025-01-01', 'C1-M6', 'C1-M8'),
    ('sp-c1m8-m7', '2025-01-01', 'C1-M7', 'C1-M8')
ON CONFLICT (id) DO NOTHING;

-- Parrainages col-3 (Tableau 4)
INSERT INTO sponsorship (id, sponsorship_date, sponsor_member_id, sponsored_member_id)
VALUES
    ('sp-c3m3-m1', '2025-01-01', 'C3-M1', 'C3-M3'),
    ('sp-c3m3-m2', '2025-01-01', 'C3-M2', 'C3-M3'),
    ('sp-c3m4-m1', '2025-01-01', 'C3-M1', 'C3-M4'),
    ('sp-c3m4-m2', '2025-01-01', 'C3-M2', 'C3-M4'),
    ('sp-c3m5-m1', '2025-01-01', 'C3-M1', 'C3-M5'),
    ('sp-c3m5-m2', '2025-01-01', 'C3-M2', 'C3-M5'),
    ('sp-c3m6-m1', '2025-01-01', 'C3-M1', 'C3-M6'),
    ('sp-c3m6-m2', '2025-01-01', 'C3-M2', 'C3-M6'),
    ('sp-c3m7-m1', '2025-01-01', 'C3-M1', 'C3-M7'),
    ('sp-c3m7-m2', '2025-01-01', 'C3-M2', 'C3-M7'),
    ('sp-c3m8-m1', '2025-01-01', 'C3-M1', 'C3-M8'),
    ('sp-c3m8-m2', '2025-01-01', 'C3-M2', 'C3-M8')
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 6. MEMBERSHIP HISTORY
-- reason CHECK: 'ADMISSION' | 'TRANSFER' | 'RESIGNATION' uniquement
-- ============================================================
INSERT INTO membership_history (id, start_date, end_date, reason, member_id, collectivity_id)
VALUES
    -- col-1
    ('mh-col1-m1', '2025-01-01', NULL, 'ADMISSION', 'C1-M1', 'col-1'),
    ('mh-col1-m2', '2025-01-01', NULL, 'ADMISSION', 'C1-M2', 'col-1'),
    ('mh-col1-m3', '2025-01-01', NULL, 'ADMISSION', 'C1-M3', 'col-1'),
    ('mh-col1-m4', '2025-01-01', NULL, 'ADMISSION', 'C1-M4', 'col-1'),
    ('mh-col1-m5', '2025-01-01', NULL, 'ADMISSION', 'C1-M5', 'col-1'),
    ('mh-col1-m6', '2025-01-01', NULL, 'ADMISSION', 'C1-M6', 'col-1'),
    ('mh-col1-m7', '2025-01-01', NULL, 'ADMISSION', 'C1-M7', 'col-1'),
    ('mh-col1-m8', '2025-01-01', NULL, 'ADMISSION', 'C1-M8', 'col-1'),
    -- col-2 (mêmes membres physiques, double appartenance)
    ('mh-col2-m1', '2025-01-01', NULL, 'ADMISSION', 'C1-M1', 'col-2'),
    ('mh-col2-m2', '2025-01-01', NULL, 'ADMISSION', 'C1-M2', 'col-2'),
    ('mh-col2-m3', '2025-01-01', NULL, 'ADMISSION', 'C1-M3', 'col-2'),
    ('mh-col2-m4', '2025-01-01', NULL, 'ADMISSION', 'C1-M4', 'col-2'),
    ('mh-col2-m5', '2025-01-01', NULL, 'ADMISSION', 'C1-M5', 'col-2'),
    ('mh-col2-m6', '2025-01-01', NULL, 'ADMISSION', 'C1-M6', 'col-2'),
    ('mh-col2-m7', '2025-01-01', NULL, 'ADMISSION', 'C1-M7', 'col-2'),
    ('mh-col2-m8', '2025-01-01', NULL, 'ADMISSION', 'C1-M8', 'col-2'),
    -- col-3
    ('mh-col3-m1', '2025-01-01', NULL, 'ADMISSION', 'C3-M1', 'col-3'),
    ('mh-col3-m2', '2025-01-01', NULL, 'ADMISSION', 'C3-M2', 'col-3'),
    ('mh-col3-m3', '2025-01-01', NULL, 'ADMISSION', 'C3-M3', 'col-3'),
    ('mh-col3-m4', '2025-01-01', NULL, 'ADMISSION', 'C3-M4', 'col-3'),
    ('mh-col3-m5', '2025-01-01', NULL, 'ADMISSION', 'C3-M5', 'col-3'),
    ('mh-col3-m6', '2025-01-01', NULL, 'ADMISSION', 'C3-M6', 'col-3'),
    ('mh-col3-m7', '2025-01-01', NULL, 'ADMISSION', 'C3-M7', 'col-3'),
    ('mh-col3-m8', '2025-01-01', NULL, 'ADMISSION', 'C3-M8', 'col-3')
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 7. COLLECTIVITY VOTES (requis par FK de collectivity_term)
-- ============================================================
INSERT INTO collectivity_vote (id, vote_date, target_year, collectivity_id)
VALUES
    ('vote-col1-2026', '2025-12-15', 2026, 'col-1'),
    ('vote-col2-2026', '2025-12-15', 2026, 'col-2'),
    ('vote-col3-2026', '2025-12-15', 2026, 'col-3')
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 8. COLLECTIVITY TERMS (mandats 2026)
-- ============================================================

-- Mandats col-1 (Tableau 2)
INSERT INTO collectivity_term (id, year, member_id, collectivity_id, position_id, vote_id)
VALUES
    ('ct-col1-m1', 2026, 'C1-M1', 'col-1', 'pos-president',        'vote-col1-2026'),
    ('ct-col1-m2', 2026, 'C1-M2', 'col-1', 'pos-vice-president',   'vote-col1-2026'),
    ('ct-col1-m3', 2026, 'C1-M3', 'col-1', 'pos-secretary',        'vote-col1-2026'),
    ('ct-col1-m4', 2026, 'C1-M4', 'col-1', 'pos-treasurer',        'vote-col1-2026'),
    ('ct-col1-m5', 2026, 'C1-M5', 'col-1', 'pos-confirmed-member', 'vote-col1-2026'),
    ('ct-col1-m6', 2026, 'C1-M6', 'col-1', 'pos-confirmed-member', 'vote-col1-2026'),
    ('ct-col1-m7', 2026, 'C1-M7', 'col-1', 'pos-confirmed-member', 'vote-col1-2026'),
    ('ct-col1-m8', 2026, 'C1-M8', 'col-1', 'pos-confirmed-member', 'vote-col1-2026')
ON CONFLICT (id) DO NOTHING;

-- Mandats col-2 (Tableau 3) — mêmes membres physiques, rôles différents
INSERT INTO collectivity_term (id, year, member_id, collectivity_id, position_id, vote_id)
VALUES
    ('ct-col2-m1', 2026, 'C1-M1', 'col-2', 'pos-confirmed-member', 'vote-col2-2026'),
    ('ct-col2-m2', 2026, 'C1-M2', 'col-2', 'pos-confirmed-member', 'vote-col2-2026'),
    ('ct-col2-m3', 2026, 'C1-M3', 'col-2', 'pos-confirmed-member', 'vote-col2-2026'),
    ('ct-col2-m4', 2026, 'C1-M4', 'col-2', 'pos-confirmed-member', 'vote-col2-2026'),
    ('ct-col2-m5', 2026, 'C1-M5', 'col-2', 'pos-president',        'vote-col2-2026'),
    ('ct-col2-m6', 2026, 'C1-M6', 'col-2', 'pos-vice-president',   'vote-col2-2026'),
    ('ct-col2-m7', 2026, 'C1-M7', 'col-2', 'pos-secretary',        'vote-col2-2026'),
    ('ct-col2-m8', 2026, 'C1-M8', 'col-2', 'pos-treasurer',        'vote-col2-2026')
ON CONFLICT (id) DO NOTHING;

-- Mandats col-3 (Tableau 4)
INSERT INTO collectivity_term (id, year, member_id, collectivity_id, position_id, vote_id)
VALUES
    ('ct-col3-m1', 2026, 'C3-M1', 'col-3', 'pos-president',        'vote-col3-2026'),
    ('ct-col3-m2', 2026, 'C3-M2', 'col-3', 'pos-vice-president',   'vote-col3-2026'),
    ('ct-col3-m3', 2026, 'C3-M3', 'col-3', 'pos-secretary',        'vote-col3-2026'),
    ('ct-col3-m4', 2026, 'C3-M4', 'col-3', 'pos-treasurer',        'vote-col3-2026'),
    ('ct-col3-m5', 2026, 'C3-M5', 'col-3', 'pos-confirmed-member', 'vote-col3-2026'),
    ('ct-col3-m6', 2026, 'C3-M6', 'col-3', 'pos-confirmed-member', 'vote-col3-2026'),
    ('ct-col3-m7', 2026, 'C3-M7', 'col-3', 'pos-confirmed-member', 'vote-col3-2026'),
    ('ct-col3-m8', 2026, 'C3-M8', 'col-3', 'pos-confirmed-member', 'vote-col3-2026')
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 9. COLLECTIVITY STRUCTURE (table bureau, ajoutée par migration V0.0.1)
-- Colonnes: id, collectivity_id, president_id, vice_president_id, treasurer_id, secretary_id
-- ============================================================
INSERT INTO collectivity_structure (id, collectivity_id, president_id, vice_president_id, treasurer_id, secretary_id)
VALUES
    ('cs-col-1', 'col-1', 'C1-M1', 'C1-M2', 'C1-M4', 'C1-M3'),
    ('cs-col-2', 'col-2', 'C1-M5', 'C1-M6', 'C1-M8', 'C1-M7'),
    ('cs-col-3', 'col-3', 'C3-M1', 'C3-M2', 'C3-M4', 'C3-M3')
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 10. MEMBERSHIP FEES (Tableaux 5, 6, 7)
-- frequency CHECK: 'WEEKLY' | 'MONTHLY' | 'ANNUALLY' | 'PUNCTUALLY'
-- status CHECK: 'ACTIVE' | 'INACTIVE'
-- ============================================================
INSERT INTO membership_fee (id, label, status, frequency, eligible_from, amount, collectivity_id)
VALUES
    ('cot-1', 'Cotisation annuelle', 'ACTIVE', 'ANNUALLY', '2026-01-01', 100000.00, 'col-1'),
    ('cot-2', 'Cotisation annuelle', 'ACTIVE', 'ANNUALLY', '2026-01-01', 100000.00, 'col-2'),
    ('cot-3', 'Cotisation annuelle', 'ACTIVE', 'ANNUALLY', '2026-01-01',  50000.00, 'col-3')
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 11. ACCOUNTS (page 16)
-- type CHECK: 'cash' | 'bank' | 'mobile_money'  ← MINUSCULE obligatoire
-- balance ajouté par migration V0.0.4, DEFAULT 0
-- ============================================================

-- col-1
INSERT INTO account (id, type, collectivity_id, federation_id, balance)
VALUES
    ('C1-A-CASH',     'cash',         'col-1', NULL, 0),
    ('C1-A-MOBILE-1', 'mobile_money', 'col-1', NULL, 0)
ON CONFLICT (id) DO NOTHING;

INSERT INTO account_mobile (account_id, holder_name, service_name, phone_number)
VALUES ('C1-A-MOBILE-1', 'Mpanorina', 'ORANGE_MONEY', '0370489612')
ON CONFLICT (account_id) DO NOTHING;

-- col-2
INSERT INTO account (id, type, collectivity_id, federation_id, balance)
VALUES
    ('C2-A-CASH',     'cash',         'col-2', NULL, 0),
    ('C2-A-MOBILE-1', 'mobile_money', 'col-2', NULL, 0)
ON CONFLICT (id) DO NOTHING;

INSERT INTO account_mobile (account_id, holder_name, service_name, phone_number)
VALUES ('C2-A-MOBILE-1', 'Dobo voalohany', 'ORANGE_MONEY', '0320489612')
ON CONFLICT (account_id) DO NOTHING;

-- col-3 (caisse uniquement)
INSERT INTO account (id, type, collectivity_id, federation_id, balance)
VALUES ('C3-A-CASH', 'cash', 'col-3', NULL, 0)
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 12. CONTRIBUTIONS (Tableaux 8 & 10)
-- type CHECK (migration V0.0.3): 'ANNUALLY' ← pas 'ANNUAL'
-- payment_method CHECK: 'CASH' | 'BANK_TRANSFER' | 'MOBILE_BANKING'
-- collectivity_id nullable depuis V0.0.4
-- membership_fee_id, account_credited_id, creation_date, label ajoutés par V0.0.3
-- ============================================================

-- col-1 (Tableau 8)
INSERT INTO contribution (id, amount, collection_date, payment_method, type, member_id, collectivity_id, membership_fee_id, account_credited_id, creation_date, label)
VALUES
    ('con-col1-m1', 100000.00, '2026-01-01', 'CASH', 'ANNUALLY', 'C1-M1', 'col-1', 'cot-1', 'C1-A-CASH', '2026-01-01', 'Cotisation annuelle 2026'),
    ('con-col1-m2', 100000.00, '2026-01-01', 'CASH', 'ANNUALLY', 'C1-M2', 'col-1', 'cot-1', 'C1-A-CASH', '2026-01-01', 'Cotisation annuelle 2026'),
    ('con-col1-m3', 100000.00, '2026-01-01', 'CASH', 'ANNUALLY', 'C1-M3', 'col-1', 'cot-1', 'C1-A-CASH', '2026-01-01', 'Cotisation annuelle 2026'),
    ('con-col1-m4', 100000.00, '2026-01-01', 'CASH', 'ANNUALLY', 'C1-M4', 'col-1', 'cot-1', 'C1-A-CASH', '2026-01-01', 'Cotisation annuelle 2026'),
    ('con-col1-m5', 100000.00, '2026-01-01', 'CASH', 'ANNUALLY', 'C1-M5', 'col-1', 'cot-1', 'C1-A-CASH', '2026-01-01', 'Cotisation annuelle 2026'),
    ('con-col1-m6', 100000.00, '2026-01-01', 'CASH', 'ANNUALLY', 'C1-M6', 'col-1', 'cot-1', 'C1-A-CASH', '2026-01-01', 'Cotisation annuelle 2026'),
    ('con-col1-m7',  60000.00, '2026-01-01', 'CASH', 'ANNUALLY', 'C1-M7', 'col-1', 'cot-1', 'C1-A-CASH', '2026-01-01', 'Cotisation annuelle 2026 (partielle)'),
    ('con-col1-m8',  90000.00, '2026-01-01', 'CASH', 'ANNUALLY', 'C1-M8', 'col-1', 'cot-1', 'C1-A-CASH', '2026-01-01', 'Cotisation annuelle 2026 (partielle)')
ON CONFLICT (id) DO NOTHING;

-- col-2 (Tableau 10) — C1-M7 et C1-M8 ont payé via mobile money
INSERT INTO contribution (id, amount, collection_date, payment_method, type, member_id, collectivity_id, membership_fee_id, account_credited_id, creation_date, label)
VALUES
    ('con-col2-m1',  60000.00, '2026-01-01', 'CASH',           'ANNUALLY', 'C1-M1', 'col-2', 'cot-2', 'C2-A-CASH',     '2026-01-01', 'Cotisation annuelle 2026 (partielle)'),
    ('con-col2-m2',  90000.00, '2026-01-01', 'CASH',           'ANNUALLY', 'C1-M2', 'col-2', 'cot-2', 'C2-A-CASH',     '2026-01-01', 'Cotisation annuelle 2026 (partielle)'),
    ('con-col2-m3', 100000.00, '2026-01-01', 'CASH',           'ANNUALLY', 'C1-M3', 'col-2', 'cot-2', 'C2-A-CASH',     '2026-01-01', 'Cotisation annuelle 2026'),
    ('con-col2-m4', 100000.00, '2026-01-01', 'CASH',           'ANNUALLY', 'C1-M4', 'col-2', 'cot-2', 'C2-A-CASH',     '2026-01-01', 'Cotisation annuelle 2026'),
    ('con-col2-m5', 100000.00, '2026-01-01', 'CASH',           'ANNUALLY', 'C1-M5', 'col-2', 'cot-2', 'C2-A-CASH',     '2026-01-01', 'Cotisation annuelle 2026'),
    ('con-col2-m6', 100000.00, '2026-01-01', 'CASH',           'ANNUALLY', 'C1-M6', 'col-2', 'cot-2', 'C2-A-CASH',     '2026-01-01', 'Cotisation annuelle 2026'),
    ('con-col2-m7',  40000.00, '2026-01-01', 'MOBILE_BANKING', 'ANNUALLY', 'C1-M7', 'col-2', 'cot-2', 'C2-A-MOBILE-1', '2026-01-01', 'Cotisation annuelle 2026 (partielle)'),
    ('con-col2-m8',  60000.00, '2026-01-01', 'MOBILE_BANKING', 'ANNUALLY', 'C1-M8', 'col-2', 'cot-2', 'C2-A-MOBILE-1', '2026-01-01', 'Cotisation annuelle 2026 (partielle)')
ON CONFLICT (id) DO NOTHING;

-- col-3 : aucun paiement (page 20)

-- ============================================================
-- 13. TRANSACTIONS (Tableaux 9 & 11)
-- Colonnes: id, account_id, amount, transaction_date, description, member_id (nullable)
-- ============================================================

-- col-1 (Tableau 9)
INSERT INTO "transaction" (id, account_id, amount, transaction_date, description, member_id)
VALUES
    ('tr-col1-m1', 'C1-A-CASH', 100000.00, '2026-01-01', 'Cotisation annuelle 2026',             'C1-M1'),
    ('tr-col1-m2', 'C1-A-CASH', 100000.00, '2026-01-01', 'Cotisation annuelle 2026',             'C1-M2'),
    ('tr-col1-m3', 'C1-A-CASH', 100000.00, '2026-01-01', 'Cotisation annuelle 2026',             'C1-M3'),
    ('tr-col1-m4', 'C1-A-CASH', 100000.00, '2026-01-01', 'Cotisation annuelle 2026',             'C1-M4'),
    ('tr-col1-m5', 'C1-A-CASH', 100000.00, '2026-01-01', 'Cotisation annuelle 2026',             'C1-M5'),
    ('tr-col1-m6', 'C1-A-CASH', 100000.00, '2026-01-01', 'Cotisation annuelle 2026',             'C1-M6'),
    ('tr-col1-m7', 'C1-A-CASH',  60000.00, '2026-01-01', 'Cotisation annuelle 2026 (partielle)', 'C1-M7'),
    ('tr-col1-m8', 'C1-A-CASH',  90000.00, '2026-01-01', 'Cotisation annuelle 2026 (partielle)', 'C1-M8')
ON CONFLICT (id) DO NOTHING;

-- col-2 (Tableau 11)
INSERT INTO "transaction" (id, account_id, amount, transaction_date, description, member_id)
VALUES
    ('tr-col2-m1', 'C2-A-CASH',      60000.00, '2026-01-01', 'Cotisation annuelle 2026 (partielle)', 'C1-M1'),
    ('tr-col2-m2', 'C2-A-CASH',      90000.00, '2026-01-01', 'Cotisation annuelle 2026 (partielle)', 'C1-M2'),
    ('tr-col2-m3', 'C2-A-CASH',     100000.00, '2026-01-01', 'Cotisation annuelle 2026',             'C1-M3'),
    ('tr-col2-m4', 'C2-A-CASH',     100000.00, '2026-01-01', 'Cotisation annuelle 2026',             'C1-M4'),
    ('tr-col2-m5', 'C2-A-CASH',     100000.00, '2026-01-01', 'Cotisation annuelle 2026',             'C1-M5'),
    ('tr-col2-m6', 'C2-A-CASH',     100000.00, '2026-01-01', 'Cotisation annuelle 2026',             'C1-M6'),
    ('tr-col2-m7', 'C2-A-MOBILE-1',  40000.00, '2026-01-01', 'Cotisation annuelle 2026 (partielle)', 'C1-M7'),
    ('tr-col2-m8', 'C2-A-MOBILE-1',  60000.00, '2026-01-01', 'Cotisation annuelle 2026 (partielle)', 'C1-M8')
ON CONFLICT (id) DO NOTHING;

-- col-3 : aucune transaction (page 20)

-- ============================================================
-- 14. MISE À JOUR DES SOLDES DES COMPTES
-- col-1 cash  : 100k×6 + 60k + 90k = 750 000
-- col-2 cash  : 60k + 90k + 100k×4 = 550 000
-- col-2 mobile: 40k + 60k           = 100 000
-- col-3       : 0 (aucun paiement)
-- ============================================================
UPDATE account SET balance = 750000.00 WHERE id = 'C1-A-CASH';
UPDATE account SET balance = 550000.00 WHERE id = 'C2-A-CASH';
UPDATE account SET balance = 100000.00 WHERE id = 'C2-A-MOBILE-1';

-- ============================================================
-- END OF SEED
-- ============================================================
