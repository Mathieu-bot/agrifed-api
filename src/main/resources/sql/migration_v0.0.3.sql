-- ============================================================
-- Migration: V0.0.3 - Membership Fee Management
-- Date: 2026-04-23
-- ============================================================

-- ------------------------------------------------------------
-- Table: membership_fee (NEW)
-- Description: Fee structure defined by collectivity
-- ------------------------------------------------------------
DROP TABLE IF EXISTS membership_fee CASCADE;

CREATE TABLE membership_fee (
    id SERIAL PRIMARY KEY,
    eligible_from DATE,
    frequency VARCHAR(20) NOT NULL CHECK (frequency IN ('weekly','monthly','annually','punctually')),
    amount DECIMAL(12,2) NOT NULL CHECK (amount > 0),
    label VARCHAR(150),
    status VARCHAR(10) NOT NULL DEFAULT 'active' CHECK (status IN ('active','inactive')),
    collectivity_id INTEGER NOT NULL,
    CONSTRAINT membership_fee_collectivity_FK FOREIGN KEY (collectivity_id) REFERENCES collectivity(id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- Modify contribution (EXISTING table)
-- ------------------------------------------------------------
ALTER TABLE contribution ADD COLUMN membership_fee_id INTEGER;
ALTER TABLE contribution ADD COLUMN account_credited_id INTEGER;
ALTER TABLE contribution ADD COLUMN creation_date DATE NOT NULL DEFAULT CURRENT_DATE;
ALTER TABLE contribution ADD COLUMN label VARCHAR(150);

ALTER TABLE contribution ADD CONSTRAINT contribution_membership_fee_FK FOREIGN KEY (membership_fee_id) REFERENCES membership_fee(id) ON DELETE SET NULL;
ALTER TABLE contribution ADD CONSTRAINT contribution_account_FK FOREIGN KEY (account_credited_id) REFERENCES account(id) ON DELETE SET NULL;

-- Fix CHECK payment_method to lowercase
ALTER TABLE contribution DROP CONSTRAINT IF EXISTS contribution_payment_method_check;
ALTER TABLE contribution ADD CONSTRAINT contribution_payment_method_check CHECK (payment_method IN ('cash', 'bank_transfer', 'mobile_banking'));

-- Fix CHECK type to add weekly, punctually
ALTER TABLE contribution DROP CONSTRAINT IF EXISTS contribution_type_check;
ALTER TABLE contribution ADD CONSTRAINT contribution_type_check CHECK (type IN ('admission', 'monthly', 'annually', 'one_time', 'weekly', 'punctually'));

-- Allow eligible_from to be NULL in membership_fee
ALTER TABLE membership_fee ALTER COLUMN eligible_from DROP NOT NULL;

-- ------------------------------------------------------------
-- Add columns to account_extended
-- ------------------------------------------------------------
ALTER TABLE account_extended ADD COLUMN bank_code VARCHAR(5);
ALTER TABLE account_extended ADD COLUMN branch_code VARCHAR(5);

-- ------------------------------------------------------------
-- DROP member_payment (replaced by contribution)
-- ------------------------------------------------------------
DROP TABLE IF EXISTS member_payment;

-- ------------------------------------------------------------
-- INDEXES
-- ------------------------------------------------------------
CREATE INDEX idx_membership_fee_collectivity ON membership_fee(collectivity_id);
CREATE INDEX idx_contribution_membership_fee ON contribution(membership_fee_id);
CREATE INDEX idx_contribution_account ON contribution(account_credited_id);

-- ============================================================
-- END OF MIGRATION V0.0.3
-- ============================================================