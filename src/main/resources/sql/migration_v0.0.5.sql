-- ============================================================
-- Migration: V0.0.5 - Add membership_type column to member table
-- Date: 2026-04-30
-- ============================================================

-- ------------------------------------------------------------
-- Add membership_type column to member table
-- ------------------------------------------------------------
ALTER TABLE member ADD COLUMN IF NOT EXISTS membership_type VARCHAR(20) CHECK (membership_type IN ('JUNIOR', 'SENIOR', 'SECRETARY', 'TREASURER', 'VICE_PRESIDENT', 'PRESIDENT'));

-- ------------------------------------------------------------
-- Update existing members to have a default membership_type
-- ------------------------------------------------------------
UPDATE member SET membership_type = 'JUNIOR' WHERE membership_type IS NULL;

-- ------------------------------------------------------------
-- Make membership_type NOT NULL after setting defaults
-- ------------------------------------------------------------
ALTER TABLE member ALTER COLUMN membership_type SET NOT NULL;

-- ============================================================
-- END OF MIGRATION V0.0.5
-- ============================================================
