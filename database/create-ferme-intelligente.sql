-- Executez ce script avec un compte PostgreSQL ayant le droit CREATEDB.
-- Exemple : psql -U postgres -d postgres -f database/create-ferme-intelligente.sql

SELECT 'CREATE DATABASE ferme_intelligente'
WHERE NOT EXISTS (
    SELECT FROM pg_database WHERE datname = 'ferme_intelligente'
)\gexec
