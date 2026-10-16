# Estructura del Monorepo y DevOps — TFG v0.7

## 1. Estructura Global del Repositorio (Monorepo)
El código de las tres aplicaciones y la infraestructura convivirá en un único repositorio de GitHub para facilitar el versionado conjunto.

```text
club-deportivo-tfg/
├── .github/
│   └── workflows/          # Pipelines de CI/CD (GitHub Actions)
├── server/                 # Backend: Java + Spring Boot
├── mobile/                 # App Socio: Flutter + Dart
├── desktop/                # App Gestión: Python + PySide6
├── docs/                   # Documentación oficial (.md del TFG)
├── docker-compose.yml      # Levantamiento de infraestructura local
├── AGENTS.md               # Reglas de IA para Antigravity/Cursor
├── README.md               # Instrucciones de setup del proyecto
└── .gitignore              # Ignorar node_modules, target, build, venv, etc.
```

## 2. Backend (Java + Spring Boot)
Arquitectura basada en Capas orientada al Dominio:
```text
server/
├── src/main/java/com/clubdeportivo/
│   ├── api/                # Controladores REST (Endpoints) y DTOs
│   │   ├── controllers/
│   │   ├── dtos/
│   │   └── exceptions/     # Manejadores globales de errores (@ControllerAdvice)
│   ├── application/        # Servicios (Casos de uso: BookingService, PaymentService)
│   ├── domain/             # Entidades del modelo de negocio (Booking, Member, Payment)
│   ├── infrastructure/     # Conexión con tecnología externa
│   │   ├── persistence/    # Repositorios JPA (Spring Data)
│   │   └── security/       # Configuración de Spring Security y filtros JWT
│   └── ClubApplication.java
├── src/main/resources/
│   ├── application.yml     # Variables de entorno y configuración
│   └── db/migration/       # Scripts de Flyway (V1__init.sql, V2__insert_roles.sql)
└── pom.xml                 # Dependencias de Maven
```

## 3. Mobile Client (Flutter + Dart)
Arquitectura Feature-First:
```text
mobile/
├── lib/
│   ├── core/               # Código compartido (Theme, API Client, Errores)
│   ├── features/           # Módulos por caso de uso (auth, bookings, profile, access)
│   └── main.dart           # Punto de entrada
├── pubspec.yaml            # Dependencias de Dart
└── .env                    # Variables de entorno (API_URL)
```

## 4. Desktop Management (Python + PySide6)
Estructura MVC (Modelo-Vista-Controlador):
```text
desktop/
├── src/
│   ├── controllers/        # Lógica de las pantallas
│   ├── models/             # Clases de datos mapeadas desde la API
│   ├── services/           # Clientes HTTP (Requests)
│   ├── views/              # Interfaces gráficas de PySide6
│   ├── utils/              # Funciones auxiliares y manejo de JWT
│   └── main.py             # Punto de entrada
├── requirements.txt        # Dependencias (requests, PySide6)
└── .env                    # Configuración (API_URL)
```

## 5. Infraestructura Local (`docker-compose.yml`)
```yaml
version: '3.8'
services:
  postgres:
    image: postgres:15-alpine
    container_name: club_db
    environment:
      POSTGRES_USER: club_admin       # Usuario para Flyway (DDL)
      POSTGRES_PASSWORD: admin_pass
      POSTGRES_DB: club_deportivo
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./docker/init-scripts:/docker-entrypoint-initdb.d

  backend:
    build: ./server
    container_name: club_backend
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/club_deportivo
      - SPRING_DATASOURCE_USERNAME=club_app_user
      - SPRING_DATASOURCE_PASSWORD=app_pass
    depends_on:
      - postgres

volumes:
  postgres_data:
```

## 6. Flujo de CI/CD (GitHub Actions)
Pipeline automático `.github/workflows/main.yml`:
1. Clonar el repositorio.
2. Configurar Java 17.
3. Ejecutar `mvn clean test` (Backend).
4. Configurar Flutter y ejecutar `flutter test` (Mobile).
5. Bloquear el *Merge* si falla alguna prueba.