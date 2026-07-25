-- Ejecutar una sola vez, antes de iniciar esta versión de la aplicación,
-- únicamente si ya existe la tabla heredada tasks con id BIGINT/IDENTITY.
-- Conserva las tareas existentes y les asigna nuevos UUID.

BEGIN;

CREATE EXTENSION IF NOT EXISTS pgcrypto;

ALTER TABLE tasks ADD COLUMN IF NOT EXISTS uuid_id UUID;
UPDATE tasks SET uuid_id = gen_random_uuid() WHERE uuid_id IS NULL;
ALTER TABLE tasks ALTER COLUMN uuid_id SET NOT NULL;

ALTER TABLE tasks DROP CONSTRAINT IF EXISTS tasks_pkey;
ALTER TABLE tasks DROP COLUMN id;
ALTER TABLE tasks RENAME COLUMN uuid_id TO id;
ALTER TABLE tasks ADD CONSTRAINT tasks_pkey PRIMARY KEY (id);

COMMIT;
