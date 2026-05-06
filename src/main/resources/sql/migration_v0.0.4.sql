-- ============================================================
-- Migration: V0.0.4 - Balance column + Uppercase enums + Indexes
-- Date: 2026-04-23
-- ============================================================

-- ------------------------------------------------------------
-- Add balance column to account table
-- ------------------------------------------------------------
ALTER TABLE account ADD COLUMN IF NOT EXISTS balance DECIMAL(12,2) DEFAULT 0;

-- ------------------------------------------------------------
-- Update member.gender to uppercase
-- ------------------------------------------------------------
UPDATE member SET gender = UPPER(gender) WHERE gender IS NOT NULL AND gender ~ '^[a-z]';

-- ------------------------------------------------------------
-- Update position.label to uppercase
-- ------------------------------------------------------------
UPDATE position SET label = UPPER(label) WHERE label IS NOT NULL AND label ~ '^[a-z]';

-- ------------------------------------------------------------
-- Update collectivity.status to uppercase
-- ------------------------------------------------------------
ALTER TABLE collectivity ALTER COLUMN status TYPE VARCHAR(20);
-- Note: Status values should be UPPERCASE: PENDING, APPROVED, REJECTED
UPDATE collectivity SET status = UPPER(status) WHERE status IS NOT NULL AND status ~ '^[a-z]';

-- ------------------------------------------------------------
-- Update position.context to uppercase
-- ------------------------------------------------------------
UPDATE position SET context = UPPER(context) WHERE context IS NOT NULL AND context ~ '^[a-z]';

-- ------------------------------------------------------------
-- Update membership_fee.status to uppercase
-- ------------------------------------------------------------
UPDATE membership_fee SET status = UPPER(status) WHERE status IS NOT NULL AND status ~ '^[a-z]';

-- ------------------------------------------------------------
-- Update account_mobile.service_name to uppercase
-- ------------------------------------------------------------
UPDATE account_mobile SET service_name = UPPER(service_name) WHERE service_name IS NOT NULL AND service_name ~ '^[a-z]';

-- ------------------------------------------------------------
-- Update contribution.payment_method to lowercase (already correct)
-- ------------------------------------------------------------
-- payment_method: cash, bank_transfer, mobile_banking (lowercase)

-- ------------------------------------------------------------
-- Update membership_history.reason to uppercase
-- ------------------------------------------------------------
UPDATE membership_history SET reason = UPPER(reason) WHERE reason IS NOT NULL AND reason ~ '^[a-z]';

-- ------------------------------------------------------------
-- Update activity.type to uppercase
-- ------------------------------------------------------------
UPDATE activity SET type = UPPER(type) WHERE type IS NOT NULL AND type ~ '^[a-z]';
-- Note: Use underscore format: GENERAL_MEETING, JUNIOR_TRAINING, EXCEPTIONAL

-- ------------------------------------------------------------
-- Update attendance.status to uppercase
-- ------------------------------------------------------------
UPDATE attendance SET status = UPPER(status) WHERE status IS NOT NULL AND status ~ '^[a-z]';

-- ------------------------------------------------------------
-- Allow collectivity_id to be nullable in contribution (for payments)
-- ------------------------------------------------------------
ALTER TABLE contribution ALTER COLUMN collectivity_id DROP NOT NULL;

-- ------------------------------------------------------------
-- INDEXES
-- ------------------------------------------------------------
CREATE INDEX IF NOT EXISTS idx_account_collectivity_balance ON account(collectivity_id, balance);
CREATE INDEX IF NOT EXISTS idx_contribution_member ON contribution(member_id);
CREATE INDEX IF NOT EXISTS idx_collectivity_vote_collectivity ON collectivity_vote(collectivity_id);
CREATE INDEX IF NOT EXISTS idx_collectivity_term_collectivity ON collectivity_term(collectivity_id);
CREATE INDEX IF NOT EXISTS idx_collectivity_term_member ON collectivity_term(member_id);
CREATE INDEX IF NOT EXISTS idx_membership_history_end_date ON membership_history(end_date);
CREATE INDEX IF NOT EXISTS idx_membership_history_member ON membership_history(member_id);

-- ============================================================
-- END OF MIGRATION V0.0.4
-- ============================================================