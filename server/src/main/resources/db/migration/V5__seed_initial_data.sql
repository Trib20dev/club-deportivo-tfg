-- V5__seed_initial_data.sql

-- 1. Insertar Usuario Administrador (Contraseña BCrypt para 'admin123')
INSERT INTO users (id, email, password_hash, status, created_at, updated_at) VALUES
    ('11111111-1111-1111-1111-111111111111', 'admin@clubdeportivo.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07Xd00DMxs.AQubh4a', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Asignar ROLE_ADMINISTRADOR (UUID a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15 definido en V2)
INSERT INTO user_roles (user_id, role_id) VALUES
    ('11111111-1111-1111-1111-111111111111', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15');

-- Datos Personales del Administrador
INSERT INTO persons (id, user_id, dni, name, last_name, phone, address, created_at, updated_at) VALUES
    ('22222222-2222-2222-2222-222222222222', '11111111-1111-1111-1111-111111111111', '00000000T', 'Administrador', 'Principal', '+34600000000', 'Calle Principal 1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 2. Insertar Usuario Socio de prueba (Contraseña BCrypt para 'socio123')
INSERT INTO users (id, email, password_hash, status, created_at, updated_at) VALUES
    ('33333333-3333-3333-3333-333333333333', 'socio1@clubdeportivo.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07Xd00DMxs.AQubh4a', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Asignar ROLE_SOCIO (UUID a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11 definido en V2)
INSERT INTO user_roles (user_id, role_id) VALUES
    ('33333333-3333-3333-3333-333333333333', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11');

-- Datos Personales del Socio
INSERT INTO persons (id, user_id, dni, name, last_name, phone, address, created_at, updated_at) VALUES
    ('44444444-4444-4444-4444-444444444444', '33333333-3333-3333-3333-333333333333', '12345678A', 'Juan', 'Pérez García', '+34611223344', 'Calle Mayor 10', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Ficha de Socio
INSERT INTO members (id, person_id, join_date, status, created_at, updated_at) VALUES
    ('55555555-5555-5555-5555-555555555555', '44444444-4444-4444-4444-444444444444', CURRENT_DATE, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Membresía del Socio
INSERT INTO memberships (id, member_id, type, discount_percentage, status, created_at, updated_at) VALUES
    ('66666666-6666-6666-6666-666666666666', '55555555-5555-5555-5555-555555555555', 'ADULT', 0.00, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 3. Insertar Instalaciones Deportivas
INSERT INTO facilities (id, name, type, capacity, price_per_hour, status, created_at, updated_at) VALUES
    ('f1111111-1111-1111-1111-111111111111', 'Pista Pádel Cristal 1', 'PADEL', 4, 12.00, 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('f2222222-2222-2222-2222-222222222222', 'Pista Pádel Cristal 2', 'PADEL', 4, 12.00, 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('f3333333-3333-3333-3333-333333333333', 'Pista Tenis Tierra Batida', 'TENNIS', 2, 15.00, 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('f4444444-4444-4444-4444-444444444444', 'Gimnasio Sala Fitness', 'GYM', 50, 0.00, 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('f5555555-5555-5555-5555-555555555555', 'Piscina Climatizada', 'POOL', 30, 0.00, 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);