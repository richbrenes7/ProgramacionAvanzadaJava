# TODO - Tarea 4: Concurrencia y Kafka

Resumen de acciones necesarias para dejar el modulo listo para produccion, con comandos reproducibles para ejecutar localmente.

## Requisitos previos

- Java 17
- Maven 3.8+
- Docker y Docker Compose, para Kafka si no se usa un broker externo
- Opcional: GitHub CLI `gh`

## Checklist

- [x] Verificar que el proyecto compila y las pruebas pasan
  - `cd tarea-4-concurrencia-kafka`
  - `mvn -B test jacoco:report`
- [x] Levantar Kafka local con Docker Compose
  - `docker compose up -d`
- [x] Ejecutar la aplicacion localmente
  - `mvn spring-boot:run`
  - o con Docker Compose: `docker compose up -d`
- [x] Verificar Producer -> Consumer
  - Validado con `KafkaIntegrationTest` usando Embedded Kafka.
- [x] Revisar logs estructurados JSON en consola
  - Configuracion en `src/main/resources/logback-spring.xml`.
- [x] Generar y revisar reporte JaCoCo
  - `target/site/jacoco/index.html`
  - `report-es/index.html`
- [ ] Anadir retry/DLQ y auditoria si se requiere tolerancia a fallos
  - Mejora opcional.
- [ ] Anadir pruebas de integracion adicionales
  - Testcontainers para Kafka recomendado como mejora opcional.
- [x] Validar imagen Docker final
  - El `Dockerfile` multi-stage ya existe.
  - Validado con `docker build -t t4-concurrencia-kafka .`.
  - Validado con `docker compose up -d`, `GET /actuator/health` y `POST /api/transacciones/publicar`.
- [x] Configurar CI/CD en GitHub Actions
  - Workflow: `.github/workflows/ci-tarea4.yml`.
  - Ejecuta build, test, analisis JaCoCo, empaquetado y deploy de imagen Docker a GHCR.

## Comandos utiles - Windows PowerShell

```powershell
cd D:\Data\ProgramacionAvanzadaJava\tarea-4-concurrencia-kafka

# Levantar Kafka y la app con Docker Compose
docker compose up -d

# Ejecutar tests y generar reporte JaCoCo
mvn -B test jacoco:report

# Ejecutar servicio sin Docker
mvn spring-boot:run

# Construir imagen Docker
docker build -t t4-concurrencia-kafka .

# Ejecutar la imagen
docker run --rm -p 8082:8082 t4-concurrencia-kafka
```

## Notas operativas

- En Windows usar `cmd /c` si hay problemas con `mvn` en PowerShell.
- El test de integracion usa `@EmbeddedKafka`.
- Si se ejecuta en CI, la accion genera JaCoCo y sube el reporte como artifact.
- Docker Desktop fue iniciado y el stack completo quedo validado con Docker Compose.
- Evidencias en `logs/docker-build-20260515-012738.log`, `logs/docker-compose-up-20260515-012920.log`, `logs/docker-health-20260515-013221.log`, `logs/docker-publicar-transaccion-20260515-013311.log` y `logs/docker-app-flow-20260515-013513.log`.
