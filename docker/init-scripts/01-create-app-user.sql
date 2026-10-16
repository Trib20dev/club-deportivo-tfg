-- Crear usuario de la aplicación para Spring Boot (DML)
CREATE USER club_app_user WITH PASSWORD 'app_pass';

-- Conceder permisos sobre la base de datos
GRANT CONNECT ON DATABASE club_deportivo TO club_app_user;
GRANT USAGE ON SCHEMA public TO club_app_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO club_app_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO club_app_user;