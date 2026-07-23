-- Fix the check constraint for motif column in declarations_bande and declarations_animaux tables
-- This script drops the old check constraint that was limiting valid motif values

-- Drop the old check constraint on declarations_bande
ALTER TABLE declarations_bande DROP CONSTRAINT IF EXISTS declarations_bande_motif_check;

-- Drop the old check constraint on declarations_animaux (in case it also has the same issue)
ALTER TABLE declarations_animaux DROP CONSTRAINT IF EXISTS declarations_animaux_motif_check;
