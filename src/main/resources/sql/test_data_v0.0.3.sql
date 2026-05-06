-- ============================================================
-- Test Data for API v0.0.3
-- ============================================================

-- ============================================================
-- 1. Create Federation
-- ============================================================
INSERT INTO federation (name) VALUES ('Fédération Agricole de Madagascar');

-- ============================================================
-- 2. Create Members (for structure and regular members)
-- ============================================================
INSERT INTO member (lastname, firstname, birth_date, gender, address, occupation, phone, email, membership_date, registration_fee_paid, membership_dues_paid)
VALUES 
-- Bureau members for collectivity 1
('Rakoto', 'Jean', '1980-05-15', 'male', 'Antananarivo', 'Agriculteur', '0321234567', 'jean.rakoto@email.com', '2024-01-15', true, true),
('Rasoa', 'Marie', '1985-08-22', 'female', 'Antananarivo', 'Secrétaire', '0322345678', 'marie.rasoa@email.com', '2024-01-15', true, true),
('Randria', 'Pierre', '1978-03-10', 'male', 'Antananarivo', 'Trésorier', '0323456789', 'pierre.randria@email.com', '2024-01-15', true, true),
('Razafy', 'Sophie', '1990-11-30', 'female', 'Antananarivo', 'VicePrésidente', '0324567890', 'sophie.razafy@email.com', '2024-01-15', true, true),

-- Regular members for collectivity 1
('Andriana', 'Paul', '1995-02-20', 'male', 'Antananarivo', 'Agriculteur', '0325678901', 'paul.andriana@email.com', '2024-02-01', true, true),
('Ramanantsoa', 'Julie', '1998-07-12', 'female', 'Antananarivo', 'Agricultrice', '0326789012', 'julie.ramanantsoa@email.com', '2024-02-01', true, true),
('Ratsimba', 'Marc', '1992-09-25', 'male', 'Antananarivo', 'Agriculteur', '0327890123', 'marc.ratsimba@email.com', '2024-02-01', true, true),

-- Members for collectivity 2
('Rakotovao', 'Aimé', '1982-04-18', 'male', 'Toamasina', 'Agriculteur', '0331234567', 'aime.rakotovao@email.com', '2024-03-01', true, true),
('Randriamanantena', 'Lina', '1988-12-05', 'female', 'Toamasina', 'Secrétaire', '0332345678', 'lina.randriamanantena@email.com', '2024-03-01', true, true),
('Ratsioro', 'Bruno', '1975-06-22', 'male', 'Toamasina', 'Trésorier', '0333456789', 'bruno.ratsioro@email.com', '2024-03-01', true, true),
('Andriamanitra', 'Claire', '1993-10-15', 'female', 'Toamasina', 'VicePrésidente', '0334567890', 'claire.andriamanitra@email.com', '2024-03-01', true, true),
('Ramanantoandro', 'Eric', '1997-01-28', 'male', 'Toamasina', 'Agriculteur', '0335678901', 'eric.ramanantoandro@email.com', '2024-03-01', true, true);

-- ============================================================
-- 3. Create Collectivities
-- ============================================================
INSERT INTO collectivity (number, name, specialty, city, creation_date, federation_id, status, location)
VALUES 
('COLL-001', 'FIJABE', 'Élevage', 'Antananarivo', '2024-01-15', 1, 'approved', 'District d''Antananarivo'),
('COLL-002', 'SAKAMASY', 'Agriculture', 'Toamasina', '2024-03-01', 1, 'approved', 'District de Toamasina');

-- ============================================================
-- 4. Create Collectivity Structures (Bureau)
-- ============================================================
INSERT INTO collectivity_structure (collectivity_id, president_id, vice_president_id, treasurer_id, secretary_id)
VALUES 
(1, 1, 4, 3, 2),  -- COLL-001: Jean, Sophie, Pierre, Marie
(2, 8, 11, 10, 9); -- COLL-002: Aimé, Claire, Bruno, Lina

-- ============================================================
-- 5. Create Membership History (link members to collectivities)
-- ============================================================
INSERT INTO membership_history (start_date, reason, member_id, collectivity_id)
VALUES 
-- Collectivity 1 members
('2024-01-15', 'admission', 1, 1),
('2024-01-15', 'admission', 2, 1),
('2024-01-15', 'admission', 3, 1),
('2024-01-15', 'admission', 4, 1),
('2024-02-01', 'admission', 5, 1),
('2024-02-01', 'admission', 6, 1),
('2024-02-01', 'admission', 7, 1),

-- Collectivity 2 members
('2024-03-01', 'admission', 8, 2),
('2024-03-01', 'admission', 9, 2),
('2024-03-01', 'admission', 10, 2),
('2024-03-01', 'admission', 11, 2),
('2024-03-01', 'admission', 12, 2);

-- ============================================================
-- 6. Create Accounts for Collectivity 1
-- ============================================================
-- Cash account
INSERT INTO account (type, collectivity_id, federation_id) VALUES ('cash', 1, NULL);

-- Bank account
INSERT INTO account (type, collectivity_id, federation_id) VALUES ('bank', 1, NULL);
INSERT INTO account_extended (account_id, holder_name, bank_name, account_number, rib_key, bank_code, branch_code)
VALUES (2, 'FIJABE', 'BOA', '12345000001123456789', '75', '12345', '00001');

-- Mobile money account
INSERT INTO account (type, collectivity_id, federation_id) VALUES ('mobile_money', 1, NULL);
INSERT INTO account_mobile (account_id, holder_name, service_name, phone_number)
VALUES (3, 'FIJABE', 'Mvola', '0321234567');

-- ============================================================
-- 7. Create Accounts for Collectivity 2
-- ============================================================
-- Cash account
INSERT INTO account (type, collectivity_id, federation_id) VALUES ('cash', 2, NULL);

-- Bank account
INSERT INTO account (type, collectivity_id, federation_id) VALUES ('bank', 2, NULL);
INSERT INTO account_extended (account_id, holder_name, bank_name, account_number, rib_key, bank_code, branch_code)
VALUES (5, 'SAKAMASY', 'BRED', '23456000001234567890', '50', '23456', '00002');

-- Mobile money account
INSERT INTO account (type, collectivity_id, federation_id) VALUES ('mobile_money', 2, NULL);
INSERT INTO account_mobile (account_id, holder_name, service_name, phone_number)
VALUES (6, 'SAKAMASY', 'Orange_Money', '0331234567');

-- ============================================================
-- 8. Create Membership Fees for Collectivity 1
-- ============================================================
INSERT INTO membership_fee (eligible_from, frequency, amount, label, status, collectivity_id)
VALUES 
('2024-01-01', 'monthly', 5000.00, 'Cotisation mensuelle', 'active', 1),
('2024-01-01', 'annually', 50000.00, 'Cotisation annuelle', 'active', 1),
('2024-01-01', 'weekly', 1000.00, 'Cotisation hebdomadaire', 'active', 1);

-- ============================================================
-- 9. Create Membership Fees for Collectivity 2
-- ============================================================
INSERT INTO membership_fee (eligible_from, frequency, amount, label, status, collectivity_id)
VALUES 
('2024-03-01', 'monthly', 3000.00, 'Cotisation mensuelle', 'active', 2),
('2024-03-01', 'annually', 30000.00, 'Cotisation annuelle', 'active', 2);

-- ============================================================
-- 10. Create Some Transactions
-- ============================================================
-- Transactions for collectivity 1
INSERT INTO "transaction" (account_id, amount, transaction_date, description, member_id)
VALUES 
(1, 50000.00, '2024-02-15', 'Cotisation annuelle - Rakoto Jean', 1),
(1, 5000.00, '2024-03-15', 'Cotisation mensuelle - Rakoto Jean', 1),
(1, 5000.00, '2024-02-20', 'Cotisation mensuelle - Andriana Paul', 5),
(2, 50000.00, '2024-02-15', 'Cotisation annuelle - Razafy Sophie', 4),
(3, 5000.00, '2024-03-10', 'Cotisation mensuelle - Ramanantsoa Julie', 6);

-- Transactions for collectivity 2
INSERT INTO "transaction" (account_id, amount, transaction_date, description, member_id)
VALUES 
(4, 30000.00, '2024-03-15', 'Cotisation annuelle - Rakotovao Aimé', 8),
(4, 3000.00, '2024-04-15', 'Cotisation mensuelle - Rakotovao Aimé', 8),
(5, 15000.00, '2024-03-20', 'Cotisation annuelle - Ratsioro Bruno', 10);

-- ============================================================
-- 11. Create Sample Contributions (for testing payments)
-- ============================================================
INSERT INTO contribution (amount, collection_date, payment_method, type, federation_percentage, member_id, collectivity_id, membership_fee_id, account_credited_id, label)
VALUES 
(5000.00, '2024-02-15', 'cash', 'monthly', 5, 1, 1, 1, 1, 'Cotisation mensuelle - Janvier'),
(50000.00, '2024-02-15', 'bank_transfer', 'annually', 5, 1, 1, 2, 2, 'Cotisation annuelle'),
(5000.00, '2024-03-15', 'mobile_banking', 'monthly', 5, 5, 1, 1, 3, 'Cotisation mensuelle - Mars'),
(3000.00, '2024-03-15', 'cash', 'monthly', 5, 8, 2, 4, 4, 'Cotisation mensuelle - Mars');
