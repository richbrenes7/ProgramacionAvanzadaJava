# Arquitectura propuesta — JavaBank Online

Arquitectura recomendada para el proyecto final (modular o monolito según equipo):

- Cliente/API Tester → API REST principal (Spring Boot)
- Capas: Controller → Service → Repository (JPA/Hibernate)
- Persistencia: PostgreSQL (docker-compose)
- Procesamiento de transacciones: `processor-transacciones` o módulo interno con `ExecutorService` y `Kafka` para eventos
- Mensajería: Kafka para alertas y auditoría asíncrona
- Observabilidad: Spring Boot Actuator y logs estructurados (logback + logstash encoder)
- Documentación: OpenAPI / Swagger (springdoc)

Diagrama simplificado:

```
Cliente/API Tester
      |
      v
API REST principal (Spring Boot)
      |
  +---+---+---+
  |   |   |   |
 Ctr  Svc Repo Kafka

DB: PostgreSQL
```
