# Tarea 4 - Concurrencia y Kafka

Modulo Spring Boot para procesamiento concurrente de transacciones bancarias con `ExecutorService`, producer/consumer Kafka, logging estructurado JSON, Docker y pipeline CI/CD.

## Objetivo

Procesar transacciones bancarias en paralelo, manejar errores por transaccion sin detener el lote, publicar eventos en Kafka y dejar el microservicio listo para ejecucion local, Docker y GitHub Actions.

## Incluye

- Procesamiento concurrente con `ThreadPoolTaskExecutor`.
- `CompletableFuture` para procesar lotes en paralelo.
- Producer Kafka para publicar transacciones.
- Consumer Kafka para procesar mensajes y publicar resultados.
- Endpoint REST para procesar lotes directamente.
- Endpoint REST para publicar transacciones a Kafka.
- Logging estructurado JSON con `logstash-logback-encoder`.
- Dockerfile multi-stage.
- `docker-compose.yml` con Zookeeper, Kafka y la app.
- Pipeline GitHub Actions con build, tests, analisis JaCoCo y deploy de imagen a GHCR.
- Pruebas unitarias e integracion con Embedded Kafka.

## Estructura

```text
tarea-4-concurrencia-kafka/
|-- Dockerfile
|-- docker-compose.yml
|-- pom.xml
|-- README.md
|-- TODO.md
|-- CUMPLIMIENTO.md
|-- report-es/
|   `-- index.html
`-- src/
    |-- main/
    |   |-- java/com/banco/t4/
    |   |   |-- config/
    |   |   |-- controller/
    |   |   |-- domain/
    |   |   |-- kafka/
    |   |   `-- service/
    |   `-- resources/
    |       |-- application.yml
    |       `-- logback-spring.xml
    `-- test/
        `-- java/com/banco/t4/
```

## Ejecucion rapida

```bash
docker compose up -d
mvn -B test jacoco:report
mvn spring-boot:run
```

Para detener el entorno Docker:

```bash
docker compose down
```

## Endpoints

### Procesar lote en memoria

```http
POST /api/transacciones/procesar-lote
Content-Type: application/json

[
  {
    "id": "tx-001",
    "cuenta": "CTA-001",
    "monto": 150.00
  }
]
```

### Publicar transaccion a Kafka

```http
POST /api/transacciones/publicar
Content-Type: application/json

{
  "id": "tx-002",
  "cuenta": "CTA-002",
  "monto": 250.00
}
```

El producer envia la transaccion al topic de entrada. El consumer procesa el mensaje con `ProcesadorTransaccionesService` y publica el resultado en el topic de salida.

## Kafka local

Con Docker Compose:

```bash
docker compose up -d
```

Desde la maquina host, la app usa por defecto:

```text
SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:29092
```

Dentro de Docker Compose, el contenedor de la app usa:

```text
SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092
```

Topics por defecto:

```text
transacciones-entrada
transacciones-resultados
```

## Docker

El `Dockerfile` es multi-stage:

- Build: `maven:3.9.4-eclipse-temurin-17`.
- Runtime: `eclipse-temurin:17-jre-alpine`.

```bash
docker build -t t4-concurrencia-kafka .
docker run --rm -p 8082:8082 t4-concurrencia-kafka
```

## CI/CD

Workflow:

```text
../.github/workflows/ci-tarea4.yml
```

El pipeline ejecuta:

1. Build.
2. Tests.
3. Analisis JaCoCo.
4. Empaquetado JAR.
5. Upload de artefactos.
6. Build y push de imagen Docker a GitHub Container Registry en eventos `push`.

En GitHub, la evidencia del pipeline se revisa en la pestaña **Actions** del repositorio, workflow `ci-tarea4.yml`. En cada ejecucion se generan artefactos de JaCoCo/JAR y, en eventos `push`, se publica la imagen en GitHub Container Registry.

Para mostrarlo localmente antes del push, se puede ejecutar el mismo flujo base:

```powershell
mvn -B test jacoco:report
docker build -t t4-concurrencia-kafka .
docker compose up -d
Invoke-RestMethod http://localhost:8082/actuator/health
Invoke-RestMethod -Uri http://localhost:8082/api/transacciones/publicar -Method Post -ContentType 'application/json' -Body '{"id":"tx-ci-cd-001","cuenta":"CTA-001","monto":125.50}'
docker compose logs --tail 220 app
```

Evidencia local generada el 2026-05-15:

```text
logs/mvn-test-jacoco-final-compliance-20260515-012117.log
logs/docker-build-20260515-012738.log
logs/docker-compose-up-20260515-012920.log
logs/docker-health-20260515-013221.log
logs/docker-publicar-transaccion-20260515-013311.log
logs/docker-app-flow-20260515-013513.log
```

## Estado validado

Validacion local realizada el 2026-05-15:

- `mvn -B test jacoco:report` finalizo correctamente.
- `docker build -t t4-concurrencia-kafka .` construyo la imagen multi-stage.
- `docker compose up -d` levanto `app`, `kafka` y `zookeeper`.
- `GET /actuator/health` respondio `UP`.
- `POST /api/transacciones/publicar` publico `tx-ci-cd-001`.
- Los logs de la app confirmaron `TRANSACCION_RECIBIDA` y `TRANSACCION_PROCESADA success=true`.

Logs principales:

```text
logs/mvn-test-jacoco-final-compliance-20260515-012117.log
logs/docker-build-20260515-012738.log
logs/docker-compose-up-20260515-012920.log
logs/docker-app-flow-20260515-013513.log
```

## Pruebas y cobertura

```bash
mvn -B test jacoco:report
```

Reporte HTML:

```text
target/site/jacoco/index.html
```

Resumen en espanol:

```text
report-es/index.html
```

## Variables utiles

- `SPRING_KAFKA_BOOTSTRAP_SERVERS`
- `app.executor.core-pool-size`
- `app.executor.max-pool-size`
- `app.executor.queue-capacity`
- `app.kafka.topic-transacciones`
- `app.kafka.topic-resultados`
