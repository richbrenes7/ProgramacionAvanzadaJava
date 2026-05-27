# Programacion Avanzada en Java

Repositorio central del curso **Programacion Avanzada en Java**. Contiene los entregables principales, documentacion tecnica, prompts de trabajo, evidencias y el proyecto final de banca digital.

## Requisitos

- Java JDK 17 o superior.
- Maven 3.9 o superior.
- Git.
- Docker Desktop opcional para PostgreSQL, Kafka y Testcontainers.

## Estructura del repositorio

```text
ProgramacionAvanzadaJava/
|-- .env.example
|-- .gitignore
|-- README.md
|-- docs/
|   |-- best-practices.md
|   `-- roadmap.md
|-- prompts/
|   `-- run-tests-and-todos.prompt.md
|-- tarea-3-api-rest/
|-- tarea-4-concurrencia-kafka/
|-- tarea3_PAJ/
`-- proyecto-final/
```

## Entregables

### Tarea 3 - API REST bancaria

Ubicacion: [`tarea-3-api-rest/`](tarea-3-api-rest/)

Proyecto Spring Boot para una API bancaria con:

- Autenticacion JWT.
- CRUD de clientes.
- Consulta de saldo por cuenta.
- Consulta de movimientos con filtros y paginacion.
- Persistencia con Spring Data JPA y H2.
- Swagger/OpenAPI.
- Pruebas unitarias, pruebas de controlador e integracion.
- Cobertura con JaCoCo.

Comandos:

```bash
cd tarea-3-api-rest
mvn test
mvn jacoco:report
mvn spring-boot:run
```

Swagger:

```text
http://localhost:8080/swagger-ui.html
```

### Tarea 4 - Concurrencia y Kafka

Ubicacion: [`tarea-4-concurrencia-kafka/`](tarea-4-concurrencia-kafka/)

Microservicio Spring Boot para procesamiento concurrente de transacciones con Kafka:

- `ThreadPoolTaskExecutor` y `CompletableFuture`.
- Producer y consumer Kafka.
- Endpoints REST para lotes y publicacion de transacciones.
- Logging estructurado.
- Dockerfile y `docker-compose.yml`.
- CI/CD con GitHub Actions.
- Pruebas con Embedded Kafka.

Comandos:

```bash
cd tarea-4-concurrencia-kafka
mvn -B test jacoco:report
docker build -t t4-concurrencia-kafka .
docker compose up -d
```

### Proyecto final - RBrenes Bank

Ubicacion: [`proyecto-final/`](proyecto-final/)

Plataforma bancaria final con backend Spring Boot, frontend estatico modular y soporte para ejecucion local o Docker.

Incluye:

- Seguridad JWT.
- API REST para clientes, cuentas, movimientos, transferencias, lotes y reportes.
- Persistencia con Spring Data JPA.
- PostgreSQL para ejecucion con contenedores.
- H2 en memoria para ejecucion local sin contenedores.
- Kafka para mensajeria de transacciones.
- Procesamiento concurrente de lotes.
- Actuator health.
- Swagger/OpenAPI.
- Pruebas de integracion con H2, Embedded Kafka y Testcontainers.
- Frontend separado por modulos: clientes, cuentas, saldos, depositos, retiros, transferencias, lotes, movimientos y reportes.

Comandos principales:

```bash
cd proyecto-final
mvn -B verify
run-local-h2.bat
```

Ejecucion con contenedores:

```bash
cd proyecto-final
docker compose up -d --build
```

URLs:

```text
Frontend: http://localhost:8080
Swagger:  http://localhost:8080/swagger-ui/index.html
Health:   http://localhost:8080/actuator/health
```

Credenciales demo del frontend:

```text
usuario: user
password: admin
```

Estado validado del proyecto final:

```text
mvn -B verify
Tests run: 7, Failures: 0, Errors: 0, Skipped: 1
BUILD SUCCESS
```

La prueba omitida depende de Docker/Testcontainers cuando Docker no esta disponible.

## Documentacion comun

- [`docs/best-practices.md`](docs/best-practices.md): convenciones de configuracion, estructura por capas, seguridad, pruebas y observabilidad.
- [`docs/roadmap.md`](docs/roadmap.md): mapa general de tareas y proyecto final.
- [`prompts/run-tests-and-todos.prompt.md`](prompts/run-tests-and-todos.prompt.md): prompt operativo para ejecutar pruebas, guardar logs, generar cobertura y reportar estado.

## Variables de entorno

El archivo [`.env.example`](.env.example) centraliza variables sugeridas para los entregables:

- Configuracion comun de aplicacion.
- Tarea 3: puerto, base de datos H2, JWT y credenciales demo.
- Tarea 4: Kafka, topicos y configuracion del pool de hilos.
- Proyecto final: PostgreSQL, Kafka, JWT y nivel de logs.

Para trabajar localmente:

```bash
cp .env.example .env
```

Luego ajustar los valores segun el entorno. No se deben subir secretos reales al repositorio.

## Flujo recomendado

1. Entrar a la carpeta del entregable.
2. Leer su `README.md`.
3. Ejecutar pruebas antes de modificar.
4. Implementar cambios pequenos y verificables.
5. Ejecutar `mvn test` o `mvn verify` segun corresponda.
6. Generar cobertura cuando aplique.
7. Guardar resultados relevantes en `logs/`, `docs/evidencias/` o el archivo de evidencia del entregable.

## Notas de mantenimiento

- Cada tarea debe mantenerse autocontenida.
- Los secretos deben vivir fuera del codigo fuente.
- Los artefactos generados por Maven (`target/`) no deben versionarse.
- Los endpoints y ejemplos de uso deben documentarse en el README de cada tarea o en `docs/`.
- El proyecto final debe mantenerse como referencia integradora del curso.
