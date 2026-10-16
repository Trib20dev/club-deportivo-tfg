# Modelo Funcional del Sistema — TFG v0.2

## 1. Actores y Permisos
- **SOCIO:** Consulta perfil, membresías, instalaciones, y gestiona sus propias reservas. Accede al club mediante tarjeta física/virtual.
- **RECEPCIONISTA:** Gestiona altas de socios, reservas de terceros, incidencias básicas y supervisa accesos.
- **MONITOR:** Consulta su horario, instalaciones asociadas a su actividad e incidencias propias.
- **JEFE_SALA:** Supervisa el estado de las instalaciones (mantenimiento), gestiona material y controla la operativa física del club.
- **ADMINISTRADOR:** Control global del sistema (usuarios, precios, reglas de negocio, membresías).

## 2. Matriz de Responsabilidades
| Funcionalidad | Socio | Recepcionista | Monitor | Jefe sala | Admin |
| :--- | :---: | :---: | :---: | :---: | :---: |
| Consultar/Modificar perfil propio | ✅ | ✅ | ✅ | ✅ | ✅ |
| Gestionar socios y membresías | ❌ | ✅ | ❌ | ❌ | ✅ |
| Crear/Cancelar reservas | Propias | ✅ | Limitado | ✅ | ✅ |
| Gestionar instalaciones/material | ❌ | Limitado | Limitado | ✅ | ✅ |
| Registrar accesos / Fichar | Propios | ✅ | Propio | Propio | ✅ |
| Configurar reglas de negocio | ❌ | ❌ | ❌ | ❌ | ✅ |

## 3. Casos de Uso Principales
**Autenticación y Usuarios:**
- `CU-AUTH-01`: Iniciar sesión (Validación de credenciales y roles).
- `CU-MEM-01`: Crear socio (Alta administrativa de perfil y membresía).

**Reservas (Core):**
- `CU-BOOK-01`: Consultar disponibilidad.
- `CU-BOOK-02`: Crear reserva (Valida disponibilidad, estado de membresía y solapamientos. Calcula el importe aplicando descuentos).
- `CU-BOOK-04`: Cancelar reserva (Aplica recargos según antelación).

**Gestión de Acompañantes:**
- Los acompañantes NO se registran lógicamente en la reserva.
- La reserva pertenece exclusivamente al socio titular. En el control de acceso, los acompañantes deben identificarse individualmente con su tarjeta para validar su membresía activa, permitiendo la entrada sin asociarse a la entidad de la reserva en base de datos.

**Control de Acceso (IoT Simulado):**
- `CU-ACCESS-01`: Registrar entrada (Valida estado del socio, existencia de reserva en el margen horario permitido, o libre acceso en zonas fitness).
- `CU-FITNESS-01`: Acceder al fitness (No requiere reserva previa, solo validación de membresía activa).

**Operativa Interna:**
- `CU-EMP-06`: Registrar fichaje (Control horario de trabajadores).
- `CU-INC-01`: Registrar incidencia (Cambios de estado de pistas o material).