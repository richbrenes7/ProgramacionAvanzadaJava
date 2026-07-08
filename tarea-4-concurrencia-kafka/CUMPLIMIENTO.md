# Cumplimiento - Tarea 4

Revision contra los puntos del enunciado.

## 1. Modulo Java concurrente con Producer Kafka, ExecutorService y logging estructurado

Estado: **cumple**

Evidencia:

- `src/main/java/com/banco/t4/config/ExecutorConfig.java`
- `src/main/java/com/banco/t4/service/ProcesadorTransaccionesService.java`
- `src/main/java/com/banco/t4/kafka/TransaccionProducer.java`
- `src/main/java/com/banco/t4/kafka/TransaccionConsumer.java`
- `src/main/resources/logback-spring.xml`

Notas:

- El procesamiento por lote usa `CompletableFuture` sobre un `ThreadPoolTaskExecutor`.
- `POST /api/transacciones/publicar` envia transacciones al topic Kafka de entrada.
- El consumer convierte el mensaje a `Transaccion`, usa `ProcesadorTransaccionesService` y publica el resultado en el topic de salida.
- Los logs salen en formato JSON mediante `logstash-logback-encoder`.

## 2. Laboratorio Docker con microservicio Spring Boot containerizado e imagen multi-stage

Estado: **cumple y validado localmente**

Evidencia:

- `Dockerfile`
- `docker-compose.yml`

Notas:

- El `Dockerfile` usa build multi-stage.
- La etapa final usa `eclipse-temurin:17-jre-alpine`.
- `docker-compose.yml` levanta Zookeeper, Kafka y la app.
- Kafka queda disponible para contenedores en `kafka:9092` y para host en `localhost:29092`.

Validacion ejecutada:

```text
docker build -t t4-concurrencia-kafka .
docker compose up -d
GET http://localhost:8082/actuator/health
POST http://localhost:8082/api/transacciones/publicar
```

Resultado local:

```text
Imagen Docker construida correctamente.
Contenedores app, kafka y zookeeper levantados correctamente.
Actuator health respondio UP.
El flujo Kafka proceso tx-ci-cd-001 con TRANSACCION_RECIBIDA y TRANSACCION_PROCESADA success=true.
```

Logs:

```text
logs/docker-build-20260515-012738.log
logs/docker-compose-up-20260515-012920.log
logs/docker-health-20260515-013221.log
logs/docker-publicar-transaccion-20260515-013311.log
logs/docker-app-flow-20260515-013513.log
```

## 3. Pipeline CI/CD build -> test -> analisis -> deploy en GitHub Actions

Estado: **cumple**

Evidencia:

- `.github/workflows/ci-tarea4.yml`

El workflow ejecuta:

- Build/test con Maven.
- Analisis JaCoCo.
- Empaquetado del JAR.
- Upload de reportes y artefactos.
- Build y push de imagen Docker a GitHub Container Registry en eventos `push`.

## Validacion de pruebas

Comando ejecutado:

```powershell
mvn -B test jacoco:report
```

Resultado:

- `KafkaIntegrationTest`: 1 prueba, 0 fallas.
- `ProcesadorTransaccionesServiceTest`: 1 prueba, 0 fallas.

Cobertura JaCoCo:

- Instrucciones: 85.45%
- Lineas: 85.07%
- Ramas: 100%
- Metodos: 87.5%
- Clases: 100%

Log:

```text
logs/mvn-test-jacoco-final-compliance-20260515-012117.log
```
