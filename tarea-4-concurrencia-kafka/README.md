# Tarea 4 — Procesamiento concurrente de transacciones con Kafka


Proyecto de ejemplo para la Tarea 4: procesamiento concurrente de transacciones bancarias.

Pasos rápidos:

```bash
# Levanta Kafka (zookeeper + kafka) y el servicio de ejemplo (construye la imagen)
docker compose up -d

# Ejecutar pruebas y generar reporte JaCoCo
mvn clean test jacoco:report

# Alternativamente iniciar el servicio sin Docker
mvn spring-boot:run
```

El proyecto incluye un `Producer` y un `Consumer` ejemplares, configuración de `ExecutorService` y pruebas unitarias básicas.
# Tarea 4 — Concurrencia y Kafka

## Objetivo

Procesar transacciones bancarias de forma concurrente usando `ExecutorService`, con manejo asíncrono de errores, logging estructurado y publicación de eventos.

## Alcance sugerido

- Procesar lotes de transacciones en paralelo.
- Validar saldo, monto y estado de cuenta.
- Manejar fallos por transacción sin detener el lote.
- Publicar resultados en Kafka o una simulación controlada.
- Agregar pruebas unitarias para validaciones y procesamiento.

## Variables de entorno

- `KAFKA_BOOTSTRAP_SERVERS`
- `KAFKA_GROUP_ID`
- `KAFKA_TOPIC_INPUT`
- `KAFKA_TOPIC_OUTPUT`
- `EXECUTOR_CORE_POOL_SIZE`
- `EXECUTOR_MAX_POOL_SIZE`
- `EXECUTOR_QUEUE_CAPACITY`

## Buenas prácticas

- Centralizar la configuración del pool de hilos.
- Registrar `transactionId`, estado y thread actual en logs.
- Evitar que una excepción detenga toda la ejecución.
- Definir timeouts y comportamiento de reintento si aplica.
