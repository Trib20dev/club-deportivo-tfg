# Reglas Maestras para Asistentes de IA (AGENTS.md)

## Contexto del Proyecto
Sistema integral de gestión para un club deportivo (Monorepo).
- **Backend:** Java 17+ y Spring Boot 3 (API REST).
- **Base de Datos:** PostgreSQL gestionado mediante Flyway.
- **Cliente Móvil (Socios):** Flutter + Dart.
- **Cliente Desktop (Gestión):** Python + PySide6.

## Reglas Arquitectónicas Estrictas
1. **Acceso a Datos:** Ningún cliente (Flutter o Python) accederá jamás directamente a PostgreSQL. Toda comunicación es vía API REST (JSON) sobre HTTPS.
2. **Identificadores:** Usa SIEMPRE `UUID v4` para las claves primarias (PK) en la base de datos y en las entidades JPA.
3. **Borrado Lógico:** No uses `DELETE` físicos en entidades de negocio (usuarios, reservas, membresías). Usa la columna `status` (`ACTIVE`, `INACTIVE`, `CANCELLED`).
4. **Mínimo Privilegio:** Spring Boot usará un usuario de BD con permisos DML (`SELECT`, `INSERT`, `UPDATE`, `DELETE`). Solo Flyway usará permisos DDL (`CREATE`, `ALTER`, `DROP`).

## Convenciones de Código y Backend
- Sigue el patrón `Controller` -> `Service` -> `Repository`.
- Usa DTOs para la entrada y salida de la API (nunca devuelvas entidades JPA directamente).
- Centraliza la lógica de negocio en la capa `Service`.
- Documenta la API usando OpenAPI / Swagger.

## Flujo de Trabajo y Git
- Usa Conventional Commits (`feat:`, `fix:`, `refactor:`, `docs:`, `chore:`).
- Haz commits atómicos (pequeños y centrados en un solo cambio).
- Cíñete a los documentos de especificación ubicados en la carpeta `docs/`.