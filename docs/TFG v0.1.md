# Guía Maestra y Arquitectura — TFG v0.1

## 1. Concepto General y Objetivo
El proyecto consiste en desarrollar un sistema integral de gestión para un club deportivo que digitalice los procesos operativos centralizando la información en una única base de datos.
El club dispone inicialmente de: 2 pistas de pádel, 1 espacio fitness, material deportivo, socios y trabajadores.

## 2. Arquitectura General
La arquitectura será cliente-servidor bajo el patrón Monorepo:
- **Mobile (Flutter + Dart):** Aplicación cliente para socios.
- **Desktop (Python + PySide6):** Aplicación de gestión administrativa.
- **Backend (Java + Spring Boot):** API REST centralizada.
- **Base de Datos (PostgreSQL):** Gestionada con Flyway.

**Regla fundamental:** Los clientes (Mobile/Desktop) nunca accederán directamente a PostgreSQL. Toda comunicación se realizará mediante la API REST (HTTPS), centralizando autenticación, validaciones y reglas de negocio.

## 3. Dominio Inicial
- **Usuarios y Seguridad:** Sistema de autenticación centralizado (Spring Security) basado en roles.
- **Socios y Membresías:** El sistema gestionará la información personal y el estado comercial del socio. El sistema calculará precios y descuentos, pero los pagos mediante pasarelas externas quedan fuera del alcance.
- **Trabajadores:** Gestión de personal, fichajes (entrada/salida) y control de horarios según roles operativos (Administrador, Recepcionista, Monitor, Jefe de Sala).
- **Instalaciones y Reservas:**
  - **Regla de negocio:** Cada reserva tendrá un único socio titular.
  - Una reserva representa una fecha y una franja horaria específica para una instalación.
  - Una instalación en estado `MANTENIMIENTO` o `NO_DISPONIBLE` no admite reservas.
- **Control de Acceso:** Se registrará la entrada y salida de los usuarios mediante la simulación de lectura de tarjetas (IoT).
- **Material e Incidencias:** Gestión básica de stock, estado y ubicación del material deportivo, así como registro de incidencias asociadas a instalaciones o material.
- **Comunicaciones:** Sistema unidireccional de avisos y ofertas desde la administración hacia las aplicaciones cliente.

## 4. Privacidad y Seguridad
**Restricción de seguridad:** El sistema no expondrá endpoints genéricos que devuelvan datos sensibles en crudo (DNI, contraseñas, IBAN). El acceso a datos filtrados dependerá estrictamente del rol del usuario autenticado, aplicando el principio de protección de datos desde el diseño.

## 5. Fases de Desarrollo (Roadmap)
- **Fase 0 — Fundación:** Configuración de Monorepo, Docker, PostgreSQL, Spring Boot y CI/CD.
- **Fase 1 — Dominio:** Definición de entidades, relaciones y casos de uso.
- **Fase 2 — Base de datos:** Migraciones Flyway y restricciones relacionales.
- **Fase 3 — API REST:** Controladores, DTOs y validaciones (OpenAPI).
- **Fase 4 — Seguridad:** JWT, roles y autorización.
- **Fase 5 — Desktop:** Funcionalidades de gestión y administración (PySide6).
- **Fase 6 — Mobile:** Funcionalidades de cliente y socio (Flutter).
- **Fase 7 y 8 — Integración y Calidad:** Pruebas E2E, integraciones y estabilización (v1.0.0).