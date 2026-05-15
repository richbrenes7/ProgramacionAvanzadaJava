# Programacion Avanzada en Java

Repositorio central del curso **Programacion Avanzada en Java**.

Contiene entregables, guias, prompts de trabajo y documentacion tecnica para las tareas del curso. La estructura esta pensada para que cada entrega pueda revisarse de forma independiente, con su propio `README.md`, codigo fuente, pruebas y archivos de apoyo.

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
|   |-- pom.xml
|   |-- README.md
|   |-- requests/
|   |   `-- api.http
|   `-- src/
|       |-- main/
|       |   |-- java/com/banco/api/
|       |   |   |-- config/
|       |   |   |-- controller/
|       |   |   |-- dto/
|       |   |   |-- entity/
|       |   |   |-- exception/
|       |   |   |-- repository/
|       |   |   |-- security/
|       |   |   `-- service/
|       |   `-- resources/
|       |       `-- application.yml
|       `-- test/
|           `-- java/com/banco/api/
|-- tarea-4-concurrencia-kafka/
|   |-- Dockerfile
|   |-- docker-compose.yml
|   |-- README.md
|   |-- TODO.md
|   |-- CUMPLIMIENTO.md
|   |-- logs/
|   |-- report-es/
|   `-- src/
|-- proyecto-final/
|   `-- README.md
`-- tarea3_PAJ/
    |-- RESULTADOS_PRUEBAS.md
    `-- report-es/
        `-- index.html
```

## Entregables

### Tarea 3 - API REST bancaria

Ubicacion: [`tarea-3-api-rest/`](tarea-3-api-rest/)

Proyecto Spring Boot para una API bancaria con:

- Autenticacion JWT.
- CRUD de clientes.
- Consulta de saldo por cuenta.
- Consulta de movimientos con filtros de fecha y paginacion.
- Persistencia con Spring Data JPA y H2.
- Swagger/OpenAPI.
- Pruebas unitarias, pruebas de controlador e integracion.
- Cobertura con JaCoCo.

Comandos principales:

```bash
cd tarea-3-api-rest
mvn test
mvn jacoco:report
mvn spring-boot:run
```

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

Coleccion HTTP:

```text
tarea-3-api-rest/requests/api.http
```

Evidencia de pruebas y cobertura:

```text
tarea3_PAJ/RESULTADOS_PRUEBAS.md
tarea3_PAJ/report-es/index.html
```

### Tarea 4 - Concurrencia y Kafka

Ubicacion: [`tarea-4-concurrencia-kafka/`](tarea-4-concurrencia-kafka/)

Microservicio Spring Boot para procesamiento concurrente de transacciones bancarias con Kafka, Docker y CI/CD. Incluye:

- Procesamiento concurrente con `ThreadPoolTaskExecutor` y `CompletableFuture`.
- Producer y consumer Kafka para transacciones y resultados.
- Endpoints REST para procesar lotes y publicar transacciones a Kafka.
- Logging estructurado JSON listo para observabilidad.
- Dockerfile multi-stage y `docker-compose.yml` con Zookeeper, Kafka y la app.
- Pipeline GitHub Actions con build, tests, analisis JaCoCo, empaquetado y deploy de imagen Docker a GHCR.
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

### Proyecto final

Ubicacion: [`proyecto-final/`](proyecto-final/)

Carpeta reservada para la plataforma bancaria final. El README interno documenta la arquitectura recomendada y los entregables esperados:

- Clientes, cuentas, movimientos y reportes.
- Seguridad JWT.
- Persistencia.
- Concurrencia.
- Docker y despliegue.
- Pruebas automatizadas.

## Documentacion comun

- [`docs/best-practices.md`](docs/best-practices.md): convenciones de configuracion, estructura por capas, seguridad, pruebas y observabilidad.
- [`docs/roadmap.md`](docs/roadmap.md): mapa general de tareas y proyecto final.
- [`prompts/run-tests-and-todos.prompt.md`](prompts/run-tests-and-todos.prompt.md): prompt operativo para ejecutar pruebas, guardar logs, generar cobertura y reportar estado.

## Variables de entorno

El archivo [`.env.example`](.env.example) centraliza variables sugeridas para los entregables:

- Configuracion comun de aplicacion.
- Tarea 3: puerto, base de datos H2, JWT y credenciales demo.
- Tarea 4: Kafka, topicos y configuracion del pool de hilos.
- Proyecto final: PostgreSQL, JWT y nivel de logs.

Para trabajar localmente:

```bash
cp .env.example .env
```

Luego ajustar los valores segun el entorno. No se deben subir secretos reales al repositorio.

## Requisitos

- Java JDK 17 o superior.
- Maven 3.9 o superior.
- Git.
- Opcional para Tarea 4: Kafka local o Docker.

## Flujo recomendado de trabajo

1. Entrar a la carpeta del entregable.
2. Leer su `README.md`.
3. Ejecutar pruebas antes de modificar.
4. Implementar cambios pequenos y verificables.
5. Ejecutar `mvn test`.
6. Generar cobertura con `mvn jacoco:report` cuando aplique.
7. Guardar resultados relevantes en una carpeta `logs/` o en el archivo de resultados del entregable.

## Notas de mantenimiento

- Cada tarea debe mantenerse autocontenida.
- Los secretos deben vivir fuera del codigo fuente.
- Los artefactos generados por Maven (`target/`) no deben versionarse.
- Los reportes de evidencia pueden guardarse en carpetas especificas cuando sean parte de una entrega.
- Los endpoints y ejemplos de uso deben documentarse en el README de cada tarea o en `requests/`.
