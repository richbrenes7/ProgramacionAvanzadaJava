# TODO — Tarea 4: Concurrencia y Kafka

Resumen de acciones necesarias para dejar el módulo listo para producción, con comandos reproducibles para ejecutar localmente.

## Requisitos previos

- Java 17
- Maven (3.8+)
- Docker & Docker Compose (para Kafka si no usas un broker externo)
- (Opcional) GitHub CLI `gh` para crear/push remoto

## Checklist (prioridad alta → baja)

- [ ] Verificar que el proyecto compila y las pruebas pasan
  - `cd tarea-4-concurrencia-kafka`
  - `mvn -B test jacoco:report`
- [ ] Levantar Kafka local con Docker Compose
  - `docker compose up -d`
- [ ] Ejecutar la aplicación localmente
  - `mvn spring-boot:run`
  - o con Docker: `docker build -t t4-concurrencia-kafka .` y `docker run -p 8082:8082 t4-concurrencia-kafka`
- [ ] Verificar Producer → Consumer (enviar ejemplo POST o usar el `TransaccionProducer` desde un cliente)
- [ ] Revisar logs estructurados JSON en la consola (configuración en `logback-spring.xml`)
- [ ] Generar y revisar reporte JaCoCo
  - `target/site/jacoco/index.html` (reporte HTML generado por Maven)
  - `report-es/index.html` (resumen traducido al español incluido en el repo)
- [ ] Añadir retry/DLQ y auditoría si se requiere tolerancia a fallos (mejora opcional)
- [ ] Añadir pruebas de integración adicionales (Testcontainers para Kafka recomendado)
- [ ] Añadir Dockerfile multi-stage (ya incluido) y validar imagen final
- [ ] Configurar CI/CD: GitHub Actions workflow ya añadido en `.github/workflows/ci-tarea4.yml`

## Comandos útiles (Windows PowerShell)

```powershell
cd D:\Data\ProgramacionAvanzadaJava\tarea-4-concurrencia-kafka
# Levantar Kafka
docker compose up -d

# Ejecutar tests y generar reporte JaCoCo
mvn -B test jacoco:report

# Ejecutar servicio
mvn spring-boot:run

# Construir imagen Docker
docker build -t t4-concurrencia-kafka .

# Ejecutar la imagen
docker run --rm -p 8082:8082 t4-concurrencia-kafka
```

## Notas operativas

- En Windows usar `cmd /c` si tienes problemas con `mvn` en PowerShell.
- El test de integración que usa `@EmbeddedKafka` requiere que las dependencias de prueba estén presentes (ya añadidas).
- Si ejecutas en CI, la acción ya generará el reporte JaCoCo y lo subirá como artifact.

## Qué puedo hacer ahora

- Ejecutar las pruebas y generar JaCoCo aquí (puedo reintentar si quieres). 
- Subir cualquier reporte HTML resultante al repo (por ejemplo `target/site/jacoco` comprimido o copiar `report-es`).

---
Archivo generado automáticamente con las tareas necesarias. Si quieres que ejecute los tests ahora, responde: `ejecuta pruebas`.
