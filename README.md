# Assignment Manager API

API REST construida con **Spring Boot 3.3.4** y **PostgreSQL** para la gestión de asignaciones universitarias y calificación asistida por IA. Proyecto del curso Programación III de la Universidad Nacional de Costa Rica (UNA).

---

## Tecnologías Usadas

| Capa | Tecnología |
|------|-----------|
| Lenguaje | Java 21 |
| Framework | Spring Boot 3.3.4 |
| ORM | Spring Data JPA / Hibernate |
| Base de datos | PostgreSQL 16 |
| Autenticación | JWT (jjwt 0.11.2) |
| Mapeo DTO | ModelMapper 3.2.1 |
| Documentación API | SpringDoc OpenAPI 2.0.2 (Swagger) |
| Correo | Spring Boot Mail (Gmail SMTP) |
| Build | Maven (wrapper incluido) |
| Contenedores | Docker Compose (PostgreSQL + pgAdmin) |

---

## Requisitos Previos

- **Java 21+ JDK** ([Descargar](https://adoptium.net/temurin/releases/?version=21))
- **Maven 3.8+** (o usar `mvnw.cmd`)
- **Docker Desktop** (para PostgreSQL y pgAdmin)
- **Git**

---

## Configuración Rápida

### 1. Clonar

```bash
git clone https://github.com/Z3E-Brian/Assignment_Manager.git
cd Assignment_Manager
```

### 2. Crear archivo `.env`

El `docker-compose.yml` usa variables de entorno desde un archivo `.env`. Crear en la raíz del proyecto:

```env
# PostgreSQL
POSTGRES_USER=admin
POSTGRES_PASSWORD=admin
POSTGRES_DB=assignment_manager_db
PGDATA=/var/lib/postgresql/data

# pgAdmin
PGADMIN_DEFAULT_EMAIL=admin@admin.com
PGADMIN_DEFAULT_PASSWORD=admin
```

> ⚠️ **Importante:** El `.env` ya está en `.gitignore` para no subir credenciales al repositorio. Si compartes el proyecto, provee un `.env.example` con valores de referencia.

### 3. Iniciar base de datos con Docker

```bash
docker compose up -d
```

Esto levanta:
- **PostgreSQL 16** en `localhost:6000`
- **pgAdmin 4** en `http://localhost:5051`

### 4. Configurar `application.properties`

El archivo `src/main/resources/application.properties` ya viene con valores por defecto:

```properties
# Base de datos
spring.datasource.url=jdbc:postgresql://localhost:6000/assignment_manager_db
spring.datasource.username=admin
spring.datasource.password=admin

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# JWT
jwt.secret=oReZWVw8m1LCkVe6Zs+Y7z/XLlKJ6JvD8oY0fhS3R8k=
jwt.access-token-expiration=120000
jwt.refresh-token-expiration=1200000

# Correo (Gmail SMTP - ejemplo)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=TU_CORREO@gmail.com
spring.mail.password=TU_CONTRASEÑA_DE_APLICACION

# Archivos
file.upload-dir=uploads
```

> **Nota:** `jwt.secret` debe cambiarse en producción. El valor actual es solo para desarrollo.

> ⚠️ Asegúrate de que `JAVA_HOME` apunte a un JDK 21. Si tienes varias versiones, `./mvnw.cmd` usará `JAVA_HOME`. Verifica con `./mvnw.cmd --version | grep "Java version"`.

### 5. Construir y ejecutar

```bash
# Con Maven wrapper
./mvnw.cmd spring-boot:run

# O empaquetar y ejecutar JAR
./mvnw.cmd clean package -DskipTests
java -jar target/Assignment_Manager-0.0.1-SNAPSHOT.jar
```

La API corre en: `http://localhost:8080`

---

## Docker: Build de la aplicación Spring Boot

Si quieres contenedorizar también la aplicación (no solo la BD):

### Crear `Dockerfile` en la raíz:

```dockerfile
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN ./mvnw dependency:go-offline -B
COPY src src
RUN ./mvnw clean package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Agregar servicio al `docker-compose.yml`:

```yaml
api:
  build: .
  ports:
    - "8080:8080"
  depends_on:
    postgres:
      condition: service_healthy
  environment:
    - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/assignment_manager_db
    - SPRING_DATASOURCE_USERNAME=${POSTGRES_USER}
    - SPRING_DATASOURCE_PASSWORD=${POSTGRES_PASSWORD}
  volumes:
    - uploads_data:/app/uploads

volumes:
  postgres_data:
  pgadmin_data:
  uploads_data:
```

### Construir y ejecutar todo:

```bash
docker compose up -d --build
```

---

## Documentación de la API (Swagger)

Una vez corriendo:

| Recurso | URL |
|---------|-----|
| Swagger UI | http://localhost:8080/doc/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |

---

## Base de Datos: Tablas y Relaciones

La aplicación crea automáticamente las tablas vía `ddl-auto=update`. Estas son las entidades:

### Diagrama de relaciones

```
University 1──N Faculty 1──N Department 1──N Career 1──N User
                                                └──N Course (por carrera)
User    N──M Course                    (vía course_users)
User    N──M Permission                (vía user_permissions)
Course  1──N Assignment
Course  1──N CourseContent
Assignment  1──N Submission
Submission  1──N File
Assignment  1──N File
CourseContent  1──N File
User    1──N Notification
AnswerAI  (tabla independiente para retroalimentación automática)
```

### Tablas generadas

| Tabla | Descripción | Claves foráneas |
|-------|-------------|-----------------|
| `universities` | Universidades | — |
| `faculties` | Facultades | `university_id` → universities |
| `departments` | Departamentos | `faculty_id` → faculties |
| `careers` | Carreras | `department_id` → departments |
| `users` | Usuarios (estudiantes, profesores, admin) | `career_id` → careers |
| `permissions` | Permisos del sistema | — |
| `user_permissions` | Permisos por usuario | `user_id` → users, `permission_id` → permissions |
| `courses` | Cursos | `professor_id` → users, `career_id` → careers |
| `course_users` | Estudiantes matriculados en cursos | `course_id` → courses, `user_id` → users |
| `assignments` | Asignaciones/tareas | `course_id` → courses |
| `course_contents` | Contenido/recursos del curso | `course_id` → courses |
| `submission` | Entregas de estudiantes | `assignment_id` → assignments, `student_id` → users, `reviewed_by` → users |
| `file` | Archivos subidos | `submission_id` → submission, `assignment_id` → assignments, `course_content_id` → course_contents |
| `answers_ai` | Retroalimentación generada por IA | — |
| `notification` | Notificaciones | `user_id` → users |

### Tipos de permiso (PermissionType)

```
CREATE_UNIVERSITIES, EDIT_UNIVERSITIES, DELETE_UNIVERSITIES, VIEW_UNIVERSITIES,
CREATE_FACULTIES, EDIT_FACULTIES, DELETE_FACULTIES, VIEW_FACULTIES,
CREATE_DEPARTMENTS, EDIT_DEPARTMENTS, DELETE_DEPARTMENTS, VIEW_DEPARTMENTS,
CREATE_CAREERS, EDIT_CAREERS, DELETE_CAREERS, VIEW_CAREERS,
CREATE_COURSES, EDIT_COURSES, DELETE_COURSES, VIEW_COURSES,
CREATE_USERS, EDIT_USERS, DELETE_USERS, VIEW_USERS,
MANAGE_PERMISSIONS, CREATE_ASSIGNMENTS, EDIT_ASSIGNMENTS,
DELETE_ASSIGNMENTS, VIEW_ASSIGNMENTS, GRADE_ASSIGNMENTS,
SUBMIT_ASSIGNMENTS, VIEW_GRADES, EDIT_PROFILE, TEACH_CLASSES,
SUBMIT_FEEDBACK, TAKE_CLASSES, REGISTER_STUDENT_COURSES
```

---

## Seed Data

Si la base de datos está vacía, el `DataSeeder` la puebla automáticamente al iniciar.

### Usuarios de prueba (contraseña: `123456`)

| Rol | Email | Permisos clave |
|-----|-------|----------------|
| Admin | admin@test.com | Todos |
| Profesor | carlos.mendoza@test.com | TEACH_CLASSES, CREATE_ASSIGNMENTS, GRADE_ASSIGNMENTS |
| Profesor | maria.vargas@test.com | TEACH_CLASSES, CREATE_ASSIGNMENTS, GRADE_ASSIGNMENTS |
| Estudiante | juan.perez@test.com | VIEW_COURSES, SUBMIT_ASSIGNMENTS, VIEW_GRADES, TAKE_CLASSES |
| Estudiante | ana.rodriguez@test.com | VIEW_COURSES, SUBMIT_ASSIGNMENTS, VIEW_GRADES, TAKE_CLASSES |
| Estudiante | luis.sandoval@test.com | VIEW_COURSES, SUBMIT_ASSIGNMENTS, VIEW_GRADES, TAKE_CLASSES |
| Estudiante | sofia.ramirez@test.com | VIEW_COURSES, SUBMIT_ASSIGNMENTS, VIEW_GRADES, TAKE_CLASSES |

### Cursos de ejemplo

| Curso | Profesor | Estudiantes |
|-------|----------|-------------|
| Programación III | C. Mendoza | Juan, Ana, Luis, Sofía |
| Bases de Datos II | M. Vargas | Juan, Ana |
| Desarrollo Web | C. Mendoza | Luis, Sofía |

---

## Endpoints Principales

### Autenticación
| Método | Endpoint | Auth |
|--------|----------|------|
| POST | `/auth/login` | ❌ Público |
| POST | `/auth/refreshToken` | ❌ Público |
| GET | `/auth/validateToken` | ✅ Requiere token |

### Usuarios
| Método | Endpoint | Auth |
|--------|----------|------|
| POST | `/api/users/create` | ❌ Público |
| GET | `/api/users` | ✅ Admin |
| PUT | `/api/users/{id}` | ✅ Admin |
| DELETE | `/api/users/{id}` | ✅ Admin |

### Cursos
| Método | Endpoint | Auth |
|--------|----------|------|
| GET | `/api/courses` | ✅ |
| GET | `/api/courses/{id}` | ✅ |
| GET | `/api/courses/professor/{id}` | ✅ |
| POST | `/api/courses` | ✅ Profesor |
| PUT | `/api/courses/{id}` | ✅ Profesor |

### Asignaciones y entregas
| Método | Endpoint | Auth |
|--------|----------|------|
| GET | `/api/assignments` | ✅ |
| POST | `/api/assignments` | ✅ Profesor |
| POST | `/api/submissions` | ✅ Estudiante |
| GET | `/api/submissions/assignment/{id}` | ✅ |

---

## Variables de Entorno (Referencia)

Crear un archivo `.env` basado en:

```env
# === PostgreSQL ===
POSTGRES_USER=admin
POSTGRES_PASSWORD=admin
POSTGRES_DB=assignment_manager_db
PGDATA=/var/lib/postgresql/data

# === pgAdmin ===
PGADMIN_DEFAULT_EMAIL=admin@admin.com
PGADMIN_DEFAULT_PASSWORD=admin
```

---

## Notas

- **Puertos:** PostgreSQL expuesto en `6000` (mapeado a `5432` interno), pgAdmin en `5051`
- **Tiempo de expiración JWT:** Access token = 2 min, Refresh token = 20 min
- **Archivos subidos:** Se almacenan en `uploads/` en la raíz del proyecto
- **Correo:** Configurado para Gmail SMTP. Usar [contraseña de aplicación](https://support.google.com/accounts/answer/185833) en lugar de la contraseña normal
