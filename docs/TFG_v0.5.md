# Diseño de Base de Datos Relacional (PostgreSQL) — TFG v0.5

## 1. Convenciones Globales del Esquema
- **Claves primarias (PK):** Utilizaremos `UUID v4` generados aleatoriamente. Protegen la privacidad (no exponen el volumen de negocio) y previenen enumeraciones maliciosas.
- **Auditoría transaccional:** Todas las tablas incluirán `created_at` y `updated_at`.
- **Borrado lógico:** Se gestionará mediante la columna `status` (`ACTIVE`, `INACTIVE`, `CANCELLED`). No se harán `DELETE` físicos para preservar la trazabilidad de accesos y pagos.

## 2. Tablas de Identidad y Perfiles
- `users`: `id` (PK), `email` (UNIQUE), `password_hash`, `status`, `created_at`.
- `roles`: `id` (PK), `name` (UNIQUE). *(Ej: ROLE_SOCIO, ROLE_ADMIN)*
- `user_roles`: `user_id` (FK), `role_id` (FK). PK compuesta.
- `persons`: `id` (PK), `user_id` (FK, nullable), `dni` (UNIQUE), `name`, `last_name`, `phone`, `address`.
- `members`: `id` (PK), `person_id` (FK, UNIQUE), `join_date`, `status`.
- `employees`: `id` (PK), `person_id` (FK), `job_title`, `status`.

## 3. Tablas de Negocio del Club
- `memberships`: `id` (PK), `member_id` (FK), `type` (BASIC, PREMIUM, HONOR), `discount_percentage`, `status`.
- `cards`: `id` (PK), `member_id` (FK), `code` (UNIQUE), `issue_date`, `status` (ACTIVE, LOST, BLOCKED).
- `facilities`: `id` (PK), `name`, `type` (PADEL, FITNESS), `is_bookable` (BOOLEAN), `status`.
- `bookings`: `id` (PK), `member_id` (FK), `facility_id` (FK), `booking_date` (DATE), `start_time` (TIME), `end_time` (TIME), `status` (CONFIRMED, CANCELLED, NO_SHOW).
- `sanctions`: `id` (PK), `member_id` (FK), `severity_level` (LEVE, GRAVE), `reason`, `status`.

## 4. Tablas de Operaciones (Pagos e IoT)
- `payments`: `id` (PK), `member_id` (FK), `amount` (DECIMAL), `status` (PENDING, PAID, FAILED), `booking_id` (FK, nullable), `membership_id` (FK, nullable), `sanction_id` (FK, nullable). *(Ver reglas de integridad).*
- `access_logs`: `id` (PK), `card_id` (FK), `facility_id` (FK, nullable), `timestamp`, `direction` (IN/OUT), `result` (GRANTED/DENIED).

## 5. Reglas de Integridad y Restricciones (Constraints) en PostgreSQL
Para blindar el negocio a nivel de base de datos, se aplicarán las siguientes reglas:
- **Consistencia temporal (Bookings):** `CHECK (start_time < end_time)`.
- **Límite de 1 reserva diaria por socio:** Se creará un Índice Único Parcial (`CREATE UNIQUE INDEX idx_one_booking_per_day ON bookings(member_id, booking_date) WHERE status = 'CONFIRMED'`). Así es imposible que un socio esquive la norma.
- **Integridad de Pagos (Arco Exclusivo):** `CHECK ( (booking_id IS NOT NULL)::int + (membership_id IS NOT NULL)::int + (sanction_id IS NOT NULL)::int = 1 )`. Garantiza que un pago corresponde estrictamente a una sola cosa (una reserva, una membresía O una sanción), protegiendo la integridad referencial.
- **Prevención de Solapamientos (Overlaps):** Se utilizará `EXCLUDE USING gist` en `bookings` para impedir que dos reservas confirmadas ocupen la misma `facility_id` en la misma fecha e intersección de horas.

## 6. Seguridad y Roles de PostgreSQL (Principio de Mínimo Privilegio)
Para garantizar la seguridad de los datos, el acceso a PostgreSQL no se realizará mediante el superusuario por defecto. Se definirán dos niveles de acceso diferenciados:
1. **Rol Administrador (Propietario del esquema):**
   - **Uso:** Utilizado exclusivamente por la herramienta de migraciones (**Flyway**) durante el despliegue.
   - **Permisos:** DDL (`CREATE`, `ALTER`, `DROP`).
2. **Rol de Aplicación (`club_app_user`):**
   - **Uso:** Utilizado por el backend (**Spring Boot**) en tiempo de ejecución.
   - **Permisos:** DML (`SELECT`, `INSERT`, `UPDATE`, `DELETE`).
   - **Restricción:** Este usuario no tendrá permisos para alterar la estructura de la base de datos (no puede hacer un `DROP TABLE` si hay una brecha de seguridad en la API).