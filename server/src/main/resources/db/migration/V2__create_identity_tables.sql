-- V2__create_identity_tables.sql

-- Tabla de Roles
CREATE TABLE roles (
    id UUID PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- Tabla de Usuarios (Credenciales)
CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla Intermedia Usuarios-Roles
CREATE TABLE user_roles (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- Tabla de Personas (Datos Personales)
CREATE TABLE persons (
    id UUID PRIMARY KEY,
    user_id UUID UNIQUE REFERENCES users(id) ON DELETE SET NULL,
    dni VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    address VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de Socios
CREATE TABLE members (
    id UUID PRIMARY KEY,
    person_id UUID NOT NULL UNIQUE REFERENCES persons(id) ON DELETE RESTRICT,
    join_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de Membresías
CREATE TABLE memberships (
    id UUID PRIMARY KEY,
    member_id UUID NOT NULL UNIQUE REFERENCES members(id) ON DELETE CASCADE,
    type VARCHAR(20) NOT NULL DEFAULT 'BASIC',
    discount_percentage DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Datos Semilla: Roles del sistema
INSERT INTO roles (id, name) VALUES
  ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'ROLE_SOCIO'),
  ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'ROLE_RECEPCIONISTA'),
  ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'ROLE_MONITOR'),
  ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 'ROLE_JEFE_SALA'),
  ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15', 'ROLE_ADMINISTRADOR');