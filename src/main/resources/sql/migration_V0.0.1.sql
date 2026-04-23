-- ============================================================
-- Migration: V0.0.1 - Add new fields for OpenAPI v0.0.1
-- Date: 2026-04-21
-- ============================================================

-- ------------------------------------------------------------
-- Add new columns to member table
-- ------------------------------------------------------------
ALTER TABLE member ADD COLUMN registration_fee_paid BOOLEAN DEFAULT FALSE;
ALTER TABLE member ADD COLUMN membership_dues_paid BOOLEAN DEFAULT FALSE;

-- ------------------------------------------------------------
-- Add new columns to collectivity table
-- ------------------------------------------------------------
ALTER TABLE collectivity ADD COLUMN location VARCHAR(200);
ALTER TABLE collectivity ADD COLUMN federation_approval BOOLEAN DEFAULT FALSE;

-- ------------------------------------------------------------
-- Create collectivity_structure table
-- Description: Bureau of a collectivity (president, vice-president, treasurer, secretary)
-- ------------------------------------------------------------
DROP TABLE IF EXISTS collectivity_structure CASCADE;


CREATE TABLE collectivity_structure (
    id VARCHAR(20) PRIMARY KEY,
    collectivity_id VARCHAR(20) NOT NULL,
    president_id VARCHAR(20),
    vice_president_id VARCHAR(20),
    treasurer_id VARCHAR(20),
    secretary_id VARCHAR(20),
    CONSTRAINT collectivity_structure_collectivity_FK FOREIGN KEY (collectivity_id) REFERENCES collectivity(id) ON DELETE CASCADE,
    CONSTRAINT collectivity_structure_president_FK FOREIGN KEY (president_id) REFERENCES member(id) ON DELETE SET NULL,
    CONSTRAINT collectivity_structure_vice_president_FK FOREIGN KEY (vice_president_id) REFERENCES member(id) ON DELETE SET NULL,
    CONSTRAINT collectivity_structure_treasurer_FK FOREIGN KEY (treasurer_id) REFERENCES member(id) ON DELETE SET NULL,
    CONSTRAINT collectivity_structure_secretary_FK FOREIGN KEY (secretary_id) REFERENCES member(id) ON DELETE SET NULL
);

-- ------------------------------------------------------------
-- INDEXES
-- ------------------------------------------------------------
CREATE INDEX idx_collectivity_structure ON collectivity_structure(collectivity_id);

-- ============================================================
-- END OF MIGRATION V0.0.1
-- ============================================================