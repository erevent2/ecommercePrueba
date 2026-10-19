# Blossom E-commerce — Backend

API REST para la aplicación de comercio electrónico. Está construida con Java 21, Spring Boot, Spring MVC, Spring Data JPA y Spring Security con autenticación JWT.

## Requisitos

- JDK 21.
- No es necesario instalar Gradle: el proyecto incluye Gradle Wrapper (`gradlew` y `gradlew.bat`).

Comprueba que Java esté disponible:

```powershell
java -version
```

## Instalación y ejecución

Abre una terminal en la carpeta `ecommerce` (la que contiene `build.gradle`).

En Windows PowerShell:

```powershell
.\gradlew.bat bootRun
```

En macOS o Linux:

```bash
./gradlew bootRun
```

La API queda disponible en `http://localhost:8080`. La documentación OpenAPI/Swagger está en `http://localhost:8080/swagger-ui.html`.

> Usa la tarea completa `bootRun`. Ejecutar `boot` puede producir un error porque Gradle encuentra varias tareas cuyo nombre comienza con esa palabra.

## Pruebas y cobertura

Ejecuta las pruebas:

```powershell
.\gradlew.bat test
```

Ejecuta la verificación de cobertura configurada en JaCoCo:

```powershell
.\gradlew.bat check
```

`check` requiere como mínimo 80% de cobertura de líneas. Para generar el informe HTML:

```powershell
.\gradlew.bat jacocoTestReport
```

El informe queda en `build/reports/jacoco/test/html/index.html`.

## Base de datos y configuración

La configuración activa de `src/main/resources/application.yml` utiliza H2 en memoria (`jdbc:h2:mem:blossom_db`). Los datos no se conservan al detener la aplicación. La consola H2 está habilitada por configuración.

El archivo `docker-compose.yml` levanta un contenedor PostgreSQL en el puerto `5432`, pero la aplicación no lo usa con la configuración actual: para conectarse a PostgreSQL hay que cambiar `spring.datasource` y configurar el driver correspondiente. No uses las credenciales de ejemplo de Docker en producción.

La clave JWT y otros valores se declaran actualmente en `application.yml` con valores de desarrollo. Sustituye estos valores por configuración segura externa antes de desplegar en un entorno real; no guardes secretos de producción en el repositorio.

La API permite por defecto solicitudes CORS desde `http://localhost` (frontend servido por Docker/Nginx) y `http://localhost:5173` (Vite en desarrollo). La lista se puede personalizar con la variable `APP_CORS_ALLOWED_ORIGINS`, separando los orígenes por comas. Configura solo los orígenes de frontend confiables.

## Endpoints principales

| Método | Ruta | Descripción |
| --- | --- | --- |
| `POST` | `/api/auth/register` | Registrar una cuenta |
| `POST` | `/api/auth/login` | Iniciar sesión y obtener un JWT |
| `GET` | `/api/products` | Buscar productos; admite `name`, `category`, `minPrice` y `maxPrice` |
| `GET` | `/api/products/{id}` | Consultar un producto |
| `POST` | `/api/products` | Crear un producto |
| `PUT` | `/api/products/{id}` | Actualizar un producto |
| `DELETE` | `/api/products/{id}` | Eliminar un producto |
| `GET` | `/api/orders` | Consultar el historial del usuario autenticado |
| `POST` | `/api/orders` | Crear una orden para el usuario autenticado |

El filtro JWT se aplica a las rutas protegidas. Actualmente las rutas de productos están permitidas sin autenticación por `SecurityConfig`; ajusta esta política si el CRUD debe requerir permisos.

## Estructura de carpetas

```text
ecommerce/
├── build.gradle                 # Plugins, dependencias, JaCoCo y verificación
├── gradlew / gradlew.bat        # Gradle Wrapper para Unix y Windows
├── docker-compose.yml           # Servicio PostgreSQL de desarrollo (no conectado por defecto)
└── src/
    ├── main/
    │   ├── java/com/blossom/ecommerce/
    │   │   ├── config/           # Seguridad, caché y OpenAPI
    │   │   ├── controller/       # Controladores REST
    │   │   ├── dto/              # Modelos de petición y respuesta
    │   │   ├── exception/        # Excepciones y respuestas globales
    │   │   ├── model/            # Entidades JPA
    │   │   ├── repository/       # Acceso a datos
    │   │   ├── security/         # JWT y carga de usuarios
    │   │   └── service/          # Lógica de negocio
    │   └── resources/
    │       └── application.yml   # Configuración de la aplicación
    └── test/
        ├── java/com/blossom/ecommerce/
        │   ├── controller/
        │   ├── exception/
        │   ├── security/
        │   └── service/
        └── resources/
            └── application.yml   # Configuración aislada para pruebas
```

## Crear el artefacto ejecutable

```powershell
.\gradlew.bat bootJar
java -jar build\libs\ecommerce-0.0.1-SNAPSHOT.jar
```
