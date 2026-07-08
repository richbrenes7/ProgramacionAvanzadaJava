# RBrenes Bank - Proyecto final

Aplicacion bancaria academica desarrollada con Java, Spring Boot y Maven para el curso Programacion Avanzada en Java. El proyecto integra API REST, seguridad JWT, persistencia, concurrencia, Kafka, Docker, documentacion tecnica, pruebas de integracion y un frontend operativo separado por modulos.

## Autoria

- Autor: Rich Brenes
- GitHub: [`richbrenes7`](https://github.com/richbrenes7)
- Proyecto: RBrenes Bank
- Curso: Programacion Avanzada en Java

## Estado del proyecto

El proyecto compila y las pruebas de integracion pasan con:

```bash
mvn -B verify
```

Resultado validado:

```text
Tests run: 7, Failures: 0, Errors: 0, Skipped: 1
BUILD SUCCESS
```

La prueba omitida corresponde a Testcontainers cuando Docker no esta disponible en el equipo.

## Modulos funcionales

- Seguridad: login con JWT en `/api/auth/login`.
- Clientes: crear, listar, consultar y actualizar clientes.
- Cuentas: crear cuentas, consultar por numero, consultar saldo y asignar cuentas a clientes.
- Movimientos: registrar depositos/retiros y consultar historial por cuenta.
- Transacciones: transferencia individual entre cuentas.
- Lotes concurrentes: procesamiento de varias transferencias con `CompletableFuture` y executor.
- Kafka: producer/consumer de transacciones, con prueba de integracion usando Embedded Kafka.
- Reportes: reporte de cartera con total de cuentas, saldo total y moneda base.
- Frontend: panel web modular para cada operacion bancaria, incluyendo asignacion de cuentas a clientes.
- Observabilidad basica: actuator health.
- Documentacion: Swagger/OpenAPI y carpeta `docs/`.

## Frontend publico y banca en linea

La interfaz esta en `src/main/resources/static/` y se sirve desde Spring Boot en:

```text
http://localhost:8080
```

El flujo visual queda separado en tres niveles:

- Pagina principal informativa del banco, publica y sin autenticacion.
- Pestana **Banca en linea**, que abre el frente de inicio de sesion.
- Panel privado despues del login, con pagina principal y acceso a cada modulo bancario.

Despues de iniciar sesion, los modulos se abren como frentes independientes dentro de la banca interna:

- Principal.
- Clientes.
- Cuentas.
- Saldos.
- Depositos.
- Retiros.
- Transferencias individuales.
- Transferencias por lote.
- Historial de movimientos.
- Reporte de cartera.

La barra superior de la banca interna conserva en todos los modulos:

- Nombre del usuario autenticado.
- Tipo de cambio visible.
- Fecha del sistema.

El portafolio visible en la banca en linea se maneja como **mis productos bancarios** del usuario autenticado. Las cuentas creadas o asociadas se guardan por usuario en el navegador y cada operacion selecciona primero el producto bancario origen. Para administrar la relacion formal del backend, el modulo **Asignaciones** permite vincular una cuenta existente a un `clienteId` y consultar las cuentas asignadas a ese cliente. El backend academico mantiene cuentas por `clienteId`; para seguridad multiusuario estricta en produccion se debe persistir la relacion usuario-cliente-cuenta y filtrar los endpoints desde el JWT.

Archivos principales:

```text
src/main/resources/static/index.html
src/main/resources/static/app.js
src/main/resources/static/styles.css
```

## Ejecucion local sin contenedores

Para correr la app con H2 en memoria, sin PostgreSQL ni Kafka local:

```bat
run-local-h2.bat
```

Luego abrir:

```text
http://localhost:8080
```

Credenciales demo:

```text
usuario: user
password: admin
```

El script desactiva el listener Kafka local para evitar warnings cuando no hay broker levantado.

## Ejecucion con Docker

Para levantar PostgreSQL, Kafka, Zookeeper y la aplicacion:

```bash
docker compose up -d --build
```

Servicios principales:

```text
App:        http://localhost:8080
PostgreSQL: localhost:5432
Kafka:      localhost:9092
Swagger:    http://localhost:8080/swagger-ui/index.html
Health:     http://localhost:8080/actuator/health
```

## Ejecucion manual con PostgreSQL local

Requisitos:

- Java 17 o superior.
- Maven 3.9 o superior.
- PostgreSQL con base `banco_db`, usuario `banco`, password `banco123`.
- Kafka local opcional en `localhost:9092`.

Comando:

```bash
mvn spring-boot:run
```

## Endpoints principales

Autenticacion:

```text
POST /api/auth/login
```

Clientes:

```text
POST /api/clientes
GET  /api/clientes
GET  /api/clientes/{id}
PUT  /api/clientes/{id}
```

Cuentas:

```text
POST /api/cuentas
GET  /api/cuentas/numero/{numero}
GET  /api/cuentas/cliente/{clienteId}
POST /api/cuentas/numero/{numero}/cliente/{clienteId}
GET  /api/cuentas/{numero}/saldo
GET  /api/cuentas/{id}/movimientos
POST /api/cuentas/transferir?origen=ACC-001&destino=ACC-002&monto=100
```

Movimientos y transacciones:

```text
POST /api/movimientos
GET  /api/movimientos/cuenta/{cuentaId}
POST /api/transacciones
POST /api/transacciones/lote
```

Reportes:

```text
GET /api/reportes/cartera
GET /actuator/health
```

## Estructura

```text
proyecto-final/
|-- Dockerfile
|-- docker-compose.yml
|-- pom.xml
|-- run-local-h2.bat
|-- docs/
|   |-- architecture.md
|   |-- endpoints.md
|   |-- diagrams/
|   |-- evidencias/
|   `-- swagger.md
|-- src/
|   |-- main/
|   |   |-- java/com/banco/core/
|   |   |   |-- cliente/
|   |   |   |-- controller/
|   |   |   |-- cuenta/
|   |   |   |-- kafka/
|   |   |   |-- movimiento/
|   |   |   |-- security/
|   |   |   |-- service/
|   |   |   `-- transaccion/
|   |   `-- resources/
|   |       |-- application.yml
|   |       `-- static/
|   `-- test/
|       |-- java/com/banco/core/
|       `-- resources/application.yml
```

## Pruebas

Ejecutar todo:

```bash
mvn -B verify
```

Ejecutar solo pruebas unitarias configuradas por Surefire:

```bash
mvn -B test
```

Las pruebas usan H2 para integracion local y Embedded Kafka para validar mensajeria sin depender de un broker externo.

## Variables y configuracion

Variables utiles:

```text
SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD
SPRING_KAFKA_BOOTSTRAP_SERVERS
APP_JWT_SECRET
APP_JWT_EXPIRATION_MS
APP_KAFKA_LISTENER_AUTO_STARTUP
```

En pruebas se activa `app.security.permit-all=true` para concentrar los escenarios en comportamiento funcional.

## Documentacion adicional

- `docs/architecture.md`: vision de arquitectura.
- `docs/endpoints.md`: resumen de endpoints.
- `docs/swagger.md`: notas de Swagger/OpenAPI.
- `docs/evidencias.md`: evidencia y comandos de validacion.
- `docs/images/diagrams/`: imagenes PNG generadas de los UML y la maquetacion.
- `docs/diagrams/`: diagramas PlantUML y maquetacion editable `.drawio`.

## Notas de entrega

Este proyecto cubre una plataforma bancaria monolitica modular con API REST, seguridad, persistencia, concurrencia, Kafka, Docker, pruebas, documentacion y frontend operativo por transaccion. Para una entrega formal, ejecutar `mvn -B verify`, abrir Swagger y guardar capturas del frontend modular si se requieren evidencias visuales.
