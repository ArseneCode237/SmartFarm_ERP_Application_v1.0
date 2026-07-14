

SELECT 'CREATE DATABASE ferme_intelligente'
WHERE NOT EXISTS (
    SELECT FROM pg_database WHERE datname = 'ferme_intelligente'
)\gexec
