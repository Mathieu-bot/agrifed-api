-- ============================================================
-- Migration: V0.0.6 - Ajout colonne relationship dans sponsorship
-- Date: 2026-05-07
-- ============================================================
-- Seul ajout structurel manquant pour les données du 6 mai 2026 :
-- la nature de la relation parrain/filleul (exigée par B-2).
-- Nullable pour ne pas invalider les anciennes lignes de parrainage.

ALTER TABLE sponsorship ADD COLUMN IF NOT EXISTS relationship VARCHAR(100);

-- ============================================================
-- END OF MIGRATION V0.0.6
-- ============================================================
