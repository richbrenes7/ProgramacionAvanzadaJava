# Programacion Avanzada en Java

Repositorio academico del curso **Programacion Avanzada en Java**.

## Autoria

- Autor: Rich Brenes
- GitHub: [`richbrenes7`](https://github.com/richbrenes7)
- Proyecto final: RBrenes Bank
- Contexto academico: Programacion Avanzada en Java

El repositorio contiene entregables independientes para revision: Tarea 3, Tarea 4 y Proyecto Final. Cada carpeta mantiene su propio `README.md`, codigo fuente, pruebas y evidencias.

## Estructura

```text
ProgramacionAvanzadaJava/
|-- README.md
|-- .gitignore
|-- .github/
|   `-- workflows/
|       |-- ci-tarea4.yml
|       `-- proyecto-final-ci-cd.yml
|-- tarea-3-api-rest/
|   |-- README.md
|   |-- pom.xml
|   |-- requests/
|   `-- src/
|-- tarea-4-concurrencia-kafka/
|   |-- README.md
|   |-- TODO.md
|   |-- CUMPLIMIENTO.md
|   |-- Dockerfile
|   |-- docker-compose.yml
|   |-- logs/
|   |-- report-es/
|   `-- src/
|-- tarea3_PAJ/
|   |-- RESULTADOS_PRUEBAS.md
|   `-- report-es/
`-- proyecto-final/
    |-- README.md
    |-- pom.xml
    |-- run-local-h2.bat
    |-- docker-compose.yml
    |-- Dockerfile
    |-- docs/
    `-- src/
```

## Tarea 3 - API REST bancaria

Ubicacion: [`tarea-3-api-rest/`](tarea-3-api-rest/)

API REST bancaria con Spring Boot. Incluye:

- Autenticacion JWT.
- CRUD de clientes.
- Consulta de saldo por cuenta.
- Consulta de movimientos con filtros y paginacion.
- Manejo centralizado de errores.
- Swagger/OpenAPI.
- Pruebas automatizadas y reporte JaCoCo.

Comandos principales:

```bash
cd tarea-3-api-rest
mvn test
mvn spring-boot:run
```

Evidencia adicional:

```text
tarea3_PAJ/RESULTADOS_PRUEBAS.md
tarea3_PAJ/report-es/index.html
```

## Tarea 4 - Concurrencia y Kafka

Ubicacion: [`tarea-4-concurrencia-kafka/`](tarea-4-concurrencia-kafka/)

Microservicio Spring Boot para procesamiento concurrente de transacciones bancarias con Kafka, Docker y CI/CD. Incluye:

- Procesamiento concurrente con `ThreadPoolTaskExecutor` y `CompletableFuture`.
- Producer y consumer Kafka.
- Endpoints REST para procesar lotes y publicar transacciones a Kafka.
- Logging estructurado JSON.
- Dockerfile multi-stage.
- `docker-compose.yml` con Zookeeper, Kafka y la app.
- Pipeline GitHub Actions con build, tests, analisis JaCoCo, empaquetado y deploy de imagen a GHCR.
- Pruebas unitarias e integracion con Embedded Kafka.

Comandos principales:

```bash
cd tarea-4-concurrencia-kafka
mvn -B test jacoco:report
docker build -t t4-concurrencia-kafka .
docker compose up -d
```

Evidencia:

```text
tarea-4-concurrencia-kafka/CUMPLIMIENTO.md
tarea-4-concurrencia-kafka/logs/
tarea-4-concurrencia-kafka/report-es/index.html
```

## Proyecto Final - RBrenes Bank

Ubicacion: [`proyecto-final/`](proyecto-final/)

Plataforma bancaria desarrollada con Spring Boot y frontend estatico modular. Integra autenticacion JWT, operaciones bancarias, persistencia JPA, mensajeria Kafka, procesamiento concurrente y pruebas automatizadas.

Funciones principales:

- Inicio y cierre de sesion con JWT.
- API REST para clientes, cuentas, movimientos, transacciones y reportes.
- Frontend por modulos: menu principal, clientes, cuentas, asignaciones, saldo, depositos, retiros, transferencias, lotes, movimientos y reportes.
- Persistencia con Spring Data JPA.
- Ejecucion local con H2.
- Ejecucion con PostgreSQL y Kafka mediante Docker Compose.
- Actuator health.
- Swagger/OpenAPI.

Comandos principales:

```bash
cd proyecto-final
run-local-h2.bat
mvn -B verify
```

URLs locales del proyecto final:

```text
Frontend: http://localhost:8080
Swagger:  http://localhost:8080/swagger-ui/index.html
Health:   http://localhost:8080/actuator/health
```

Credenciales demo:

```text
usuario: user
password: admin
```

## CI/CD

Workflows disponibles:

- [`.github/workflows/ci-tarea4.yml`](.github/workflows/ci-tarea4.yml): build, tests, analisis JaCoCo, empaquetado y publicacion de imagen Docker para Tarea 4.
- [`.github/workflows/proyecto-final-ci-cd.yml`](.github/workflows/proyecto-final-ci-cd.yml): build/test del proyecto final, reportes, imagen Docker y deploy opcional a AWS o Azure.

## Requisitos

- Java JDK 17 o superior.
- Maven 3.9 o superior.
- Git.
- Docker Desktop para Tarea 4, Kafka, PostgreSQL o pruebas con contenedores.

## Mantenimiento

- Cada entregable debe mantenerse autocontenido.
- Ejecutar las pruebas dentro de la carpeta del entregable antes de hacer push.
- No subir secretos reales al repositorio.
- No versionar artefactos generados por Maven (`target/`).
- Guardar evidencias relevantes en `logs/`, `report-es/` o archivos de resultados del entregable.
