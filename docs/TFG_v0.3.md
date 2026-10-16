# Guía de Decisiones y Reglas de Negocio — TFG v0.3

## 1. Reglas Horarias y Temporales
- **Horario de Operación:** El club funciona de lunes a sábado de 08:00 a 22:00.
- **Reservas:** Tienen una duración estricta de 1 hora. La última franja reservable es de 20:00 a 21:00.
- **Antelación:** Se permite reservar con un máximo de 7 días de antelación.
- **Límite Diario:** Un socio puede tener como máximo 1 reserva confirmada por día.
- **Margen de Acceso:** El titular tiene un margen de gracia de ±2 minutos respecto a la hora de inicio de la reserva para registrar su acceso físico.

## 2. Membresías y Ofertas
- **Tipos de Membresía:** BÁSICA (0%), PREMIUM (10%), HONOR (20%).
- **Prioridad de Descuentos:** Las promociones u ofertas puntuales no son acumulables con el descuento de la membresía. El sistema aplicará automáticamente el descuento que resulte mayor y más beneficioso para el socio.

## 3. Control de Asistencia y Penalizaciones
- **Cancelación Anticipada (> 24h):** 0% de penalización.
- **Cancelación Tardía (Mismo día):** Genera un recargo del 75% sobre el importe de la reserva, registrado como pago pendiente y sanción de falta leve.
- **Ausencia (No-Show):** A los 15 minutos de iniciada la reserva sin registro de entrada, se envía una notificación de aviso. Si la franja finaliza sin acceso, se cobra el 100% de penalización (pago pendiente) y se registra una falta leve.

## 4. Control de Acceso y Acompañantes
- **Acceso Físico:** Se realiza mediante tarjeta física/virtual mediante simulación IoT.
- **Verificación de Acompañantes:** El titular indica en recepción/acceso la reserva. Cada acompañante debe validar su propia tarjeta de socio. No se crean registros de acompañantes en la base de datos de reservas.
- **Acceso Sin Reserva:** Si un socio intenta acceder a una pista sin reserva confirmada, la puerta deniega el paso.

## 5. Espacio Fitness
- **Acceso Directo:** La zona de gimnasio/fitness no requiere reserva previa.
- **Trazabilidad:** El socio accede escaneando su tarjeta. El sistema registra la hora de entrada y salida para calcular la ocupación y el tiempo de permanencia.

## 6. Trabajadores y Fichajes
- **Registro de Jornada:** Los trabajadores registran entrada y salida en la aplicación. El sistema calcula las horas trabajadas comparándolas con su cuadrante previsto.
- **Protección de Datos:** Las contraseñas se almacenan mediante hash seguro (BCrypt). Los datos sensibles (DNI, IBAN, dirección) están restringidos según el rol.