# 🏆 Sistema Integral de Gestión para Club Deportivo

Una solución tecnológica moderna y multiplataforma diseñada para digitalizar, automatizar y simplificar la administración de clubes deportivos, la gestión de instalaciones y la reserva de pistas.

---

## 📌 Visión General del Proyecto

El **Sistema de Gestión de Club Deportivo** resuelve la problemática habitual de la gestión manual de instalaciones, solapamiento de horarios y administración de socios. Proporciona una arquitectura centralizada basada en una **API REST stateless** conectada a clientes especializados (aplicación móvil para socios y panel de administración para gestión de personal).

---

## ✨ Funcionalidades Principales

### 👤 1. Gestión de Socios y Perfiles
- **Registro y Autenticación Segura:** Acceso personalizado con cifrado de credenciales y tokens de sesión JWT.
- **Perfiles de Socio:** Administración de datos personales, estado de suscripción (`ACTIVE`, `INACTIVE`, `SUSPENDED`) y tipos de membresía con descuentos aplicables.
- **Historial de Actividad:** Consulta rápida de reservas previas y cuotas.

### 🎾 2. Gestión de Instalaciones y Pistas
- **Catálogo de Instalaciones:** Configuración de pistas de pádel, tenis, gimnasio, piscina, etc.
- **Control de Tarifas y Capacidad:** Definición de precios por hora e intervalo horario.
- **Mantenimiento y Disponibilidad:** Asignación de estados (`AVAILABLE`, `MAINTENANCE`, `OUT_OF_SERVICE`) para evitar reservas no deseadas.

### 📅 3. Sistema de Reservas Inteligente
- **Calendario en Tiempo Real:** Verificación instantánea de disponibilidad de pistas y rangos horarios.
- **Prevención de Conflictos:** Algoritmo de validación de horarios que impide dobles reservas.
- **Gestión de Cancelaciones:** Políticas flexibles de cancelación y modificación de turnos.

### 🔐 4. Control de Acceso basado en Roles (RBAC)
- **Socio:** Acceso a perfil personal, consulta de instalaciones y gestión de reservas propias.
- **Recepcionista / Jefe de Sala:** Búsqueda de socios, gestión presencial de reservas y supervisión de pistas.
- **Administrador:** Control total sobre usuarios, tipos de membresía, estado de instalaciones y métricas del club.

---

## 🏗️ Arquitectura Técnica

El proyecto se estructura en una arquitectura en capas, priorizando la escalabilidad, la mantenibilidad y la seguridad:

```text
[ App Móvil (Socios) ]        [ App Escritorio (Personal) ]
           │                                │
           └──────────────┬─────────────────┘
                          │ (HTTPS / JSON / JWT)
                          ▼
             ┌─────────────────────────┐
             │   API REST Spring Boot  │
             ├─────────────────────────┤
             │   Security & JWT        │
             │   Business Logic        │
             │   Spring Data JPA       │
             └────────────┬────────────┘
                          │
                          ▼
             ┌─────────────────────────┐
             │   PostgreSQL Database   │
             └─────────────────────────┘
```

### 🛠️ Tecnologías Utilizadas

- **Backend:** Java 17, Spring Boot 3, Spring Security, Spring Data JPA, Lombok, Flyway (Migraciones de BDD).
- **Seguridad:** Autenticación Stateless basada en tokens JWT (HMAC-SHA256).
- **Base de Datos:** PostgreSQL.
- **Clientes (En desarrollo):**
  - **Aplicación Móvil:** Enfocada en la experiencia del socio (reservas rápidas y perfil).
  - **Aplicación de Escritorio/Web:** Enfocada en el panel de control del personal administrativo.

---

## 🚀 Hoja de Ruta del Desarrollo (Roadmap)

- [x] **Fase 1: Infraestructura y Base de Datos**
  - Esquema relacional optimizado con Flyway (`V1` a `V5`).
  - Manejo global de excepciones estructurado (`ApiResponse`).
- [x] **Fase 2: Autenticación y Seguridad**
  - Implementación de Spring Security y endpoints JWT (`/api/v1/auth/login`, `/register`).
- [x] **Fase 3: Módulo de Socios**
  - API de perfiles, modificación de datos y gestión de estados (`/api/v1/members`).
- [ ] **Fase 4: Módulo de Instalaciones y Reservas**
  - Gestión de pistas (`/api/v1/facilities`) y motor de reservas (`/api/v1/bookings`).
- [ ] **Fase 5: Clientes Multiplataforma**
  - Desarrollo de la App Móvil para socios.
  - Desarrollo del Panel de Administración para escritorio.

---

## 📄 Licencia y Entorno

Este proyecto ha sido desarrollado como Trabajo de Fin de Grado (TFG), aplicando estándares profesionales de ingeniería de software, pruebas unitarias e integración continua.
