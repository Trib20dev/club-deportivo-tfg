-- V3__create_facilities_and_bookings.sql

-- Tabla de Instalaciones
CREATE TABLE facilities (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    type VARCHAR(50) NOT NULL,
    capacity INT NOT NULL,
    price_per_hour DECIMAL(7,2) NOT NULL DEFAULT 0.00,
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de Reservas
CREATE TABLE bookings (
    id UUID PRIMARY KEY,
    member_id UUID NOT NULL REFERENCES members(id) ON DELETE CASCADE,
    facility_id UUID NOT NULL REFERENCES facilities(id) ON DELETE CASCADE,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED',
    total_price DECIMAL(7,2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices para optimizar búsquedas por rango de fecha en reservas
CREATE INDEX idx_bookings_facility_dates ON bookings(facility_id, start_time, end_time);
CREATE INDEX idx_bookings_member ON bookings(member_id);