-- Crear usuario de aplicación si no existe (ejecutado por Flyway como club_admin)
DO $$ 
BEGIN 
   IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'club_app_user') THEN 
      CREATE USER club_app_user WITH PASSWORD 'app_pass'; 
   END IF; 
END $$;

GRANT CONNECT ON DATABASE club_deportivo TO club_app_user;
GRANT USAGE ON SCHEMA public TO club_app_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO club_app_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO club_app_user;

-- Tabla de prueba para validar la migración inicial
CREATE TABLE IF NOT EXISTS test_connection (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);