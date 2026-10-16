# Diseño del Contrato de la API REST (OpenAPI) — TFG v0.6

## 1. Convenciones y Estándares
- **Base URL:** `/api/v1`
- **Seguridad:** Tokens JWT en cabecera `Authorization: Bearer <token>`.
- **Respuestas de Error:** Estructura JSON estandarizada (RFC 7807) con código HTTP, mensaje legible y código interno (ej. `BOOKING_OVERLAP`).
- **Paginación y Filtros:** Se usarán *query parameters* para listados (ej. `?page=0&size=20&status=ACTIVE`).

## 2. Autenticación y Seguridad
- `POST /auth/login` → Autentica al usuario (socio o trabajador) y devuelve el JWT con sus roles.
- `POST /auth/refresh` → Renueva el token caducado.
- `POST /auth/password-reset` → Inicia recuperación de contraseña.

## 3. Endpoints de Socios (Mobile Client)
*(Requieren rol `SOCIO`)*
- `GET /members/me` → Obtiene perfil personal, tarjeta activa, tipo de membresía y descuentos.
- `PUT /members/me` → Actualiza datos de contacto permitidos.
- `GET /facilities/availability?date=YYYY-MM-DD` → Devuelve pistas y franjas horarias libres.
- `POST /bookings/preview` → **Simulación:** Calcula precio final desglosado (base - descuentos) sin guardar.
- `POST /bookings` → **Confirmación:** Crea la reserva final.
- `GET /members/me/bookings` → Listado de reservas propias (pasadas y futuras).
- `POST /bookings/{id}/cancel` → Cancela la reserva (aplica 75% si es el mismo día).
- `GET /members/me/payments?status=PENDING` → Consulta deudas activas.
- `POST /payments/{id}/pay` → Simula el pago de una deuda desde la app (estado a PAID).

## 4. Endpoints de Gestión Administrativa (Desktop Management)
*(Requieren roles operativos: `RECEPCIONISTA`, `JEFE_SALA`, `ADMINISTRADOR`)*

**Gestión de Socios, Membresías y Tarjetas:**
- `GET /members` → Listado global de socios.
- `POST /members` → Alta administrativa de un nuevo socio.
- `GET /members/{id}` → Ficha completa de un socio.
- `PUT /members/{id}` → Modifica datos del socio (recepción).
- `PUT /members/{id}/membership` → Modificar estado o tipo de membresía (Premium, Honor).
- `POST /members/{id}/cards` → Emitir una nueva tarjeta física/virtual al socio.
- `PUT /cards/{id}/status` → Bloquear tarjeta por pérdida o robo.

**Gestión de Trabajadores:**
- `GET /employees` → Listado de plantilla.
- `POST /employees` → Alta de trabajador.
- `PUT /employees/{id}` → Modificación de datos laborales.
- `PUT /employees/{id}/roles` → Asignación de roles de sistema (ej. conceder rol `RECEPCIONISTA`).

**Operativa Diaria (Reservas, Accesos y Finanzas):**
- `GET /bookings` → Vista global de ocupación de pistas para el personal.
- `GET /access-logs` → Historial de quién ha entrado/salido del club.
- `GET /payments` → Listado global de pagos y deudas.
- `POST /payments/{id}/resolve` → Recepción marca una deuda como pagada en el mostrador (efectivo/TPV).
- `PUT /facilities/{id}/status` → Cambiar estado de instalación (DISPONIBLE, MANTENIMIENTO).

**Sanciones y Disciplina:**
- `GET /sanctions` → Listado de sanciones.
- `POST /members/{id}/sanctions` → Registro manual de una falta o suspensión a un socio.
- `PUT /sanctions/{id}/status` → Actualiza el estado de una sanción.

## 5. Configuración de Precios y Negocio (Solo Administrador)
- `PUT /facilities/{id}/price` → Actualiza el precio base por hora de una instalación.
- `GET /settings/pricing` → Consulta reglas globales (descuento Premium, % de penalizaciones).
- `PUT /settings/pricing` → Modifica las reglas globales de negocio dinámicamente.

## 6. Endpoints IoT (Control de Acceso)
- `POST /access/scan` → Recibe `{ "cardCode", "facilityId" }`. Valida tarjeta, estado de membresía, existencia de reservas en margen (±2 min) y registra el `access_log`. Devuelve `GRANTED` o `DENIED`.

## 7. Trabajadores (Portal Empleado)
- `GET /employees/me/schedule` → Consulta de horario previsto.
- `POST /attendance/clock-in` → Fichaje de entrada.
- `POST /attendance/clock-out` → Fichaje de salida.
- `GET /attendance` → Administrador revisa el registro de horas.

## 8. Operativa: Incidencias y Material
- `GET /incidents` → Listado de incidencias activas.
- `POST /incidents` → Registrar una incidencia (ej. "Cristal roto").
- `PUT /incidents/{id}/status` → Actualizar estado de resolución.
- `GET /equipment` → Inventario de material.
- `POST /equipment` → Añadir nuevo lote de material.
- `PUT /equipment/{id}` → Actualizar cantidades o cambiar ubicación.

## 9. Comunicaciones
- `POST /communications` → Administrador publica un aviso u oferta.
- `GET /communications` → Los socios reciben el listado de avisos y ofertas activas en su app.