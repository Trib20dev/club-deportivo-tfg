-- V4__create_access_control_and_payments.sql

-- Tabla de Control de Accesos (Torniquetes / Fichajes)
CREATE TABLE access_logs (
    id UUID PRIMARY KEY,
    member_id UUID NOT NULL REFERENCES members(id) ON DELETE CASCADE,
    access_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    access_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    denial_reason VARCHAR(255)
);

-- Tabla de Pagos y Cuotas
CREATE TABLE payments (
    id UUID PRIMARY KEY,
    member_id UUID NOT NULL REFERENCES members(id) ON DELETE CASCADE,
    amount DECIMAL(7,2) NOT NULL DEFAULT 0.00,
    concept VARCHAR(255) NOT NULL,
    payment_date TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    payment_method VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices de rendimiento
CREATE INDEX idx_access_logs_member ON access_logs(member_id, access_time);
CREATE INDEX idx_payments_member ON payments(member_id, status);