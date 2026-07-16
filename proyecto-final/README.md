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
Tests run: 8, Failures: 0, Errors: 0, Skipped: 1
BUILD SUCCESS
```

La prueba omitida corresponde a Testcontainers cuando Docker no esta disponible en el equipo.

## Modulos funcionales

- Seguridad: login con JWT en `/api/auth/login`, usuarios persistidos en tabla `usuarios`, roles `ADMIN`/`USER`, relacion usuario-cliente y recuperacion demo de contrasena desde login.
- Clientes: crear, listar, consultar y actualizar clientes.
- Productos bancarios: crear cuentas/productos, consultar por numero, consultar saldo y asignar productos a clientes con numeracion aleatoria generada por backend.
- Movimientos: registrar retiros y consultar historial por cuenta; los depositos/notas de credito se originan por transferencias recibidas u operaciones administrativas.
- Transacciones: transferencia individual entre cuentas, validando que la cuenta origen pertenezca al usuario autenticado.
- Lotes concurrentes: procesamiento de varias transferencias con `CompletableFuture` y executor.
- Kafka: producer/consumer de transacciones, con prueba de integracion usando Embedded Kafka.
- Administracion: gestion de usuarios, reset de contrasena y generacion/asociacion de productos a clientes.
- Reportes: cartera financiera para administradores y panel Sistema independiente para registros operativos, salud, logs y catalogo de APIs/metodos.
- Frontend: panel web modular separado por perfil; clientes operan solo sus productos y administradores gestionan clientes, usuarios, productos y sistema.
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
- Cuentas propias.
- Saldos.
- Retiros.
- Transferencias individuales con resolucion del beneficiario por numero de cuenta.
- Transferencias por lote.
- Historial de movimientos propios.
- Administrador, visible solo para rol `ADMIN`.
- Clientes y asignaciones, visibles solo para rol `ADMIN`.
- Reporte de cartera, visible solo para rol `ADMIN`.
- Sistema, visible solo para rol `ADMIN`, con registros/APIs, health y logs.

La barra superior de la banca interna conserva en todos los modulos:

- Nombre del usuario autenticado.
- Tipo de cambio visible.
- Fecha del sistema.

El portafolio visible en la banca en linea se maneja como **mis productos bancarios** del usuario autenticado. El backend filtra los productos desde el `clienteId` asociado al usuario JWT. Los usuarios cliente no visualizan el ID del cliente, la tabla general de clientes ni las asignaciones; esas vistas son exclusivas del administrador. En transferencias, el usuario ingresa el numero de cuenta destino y el sistema resuelve automaticamente el nombre del beneficiario antes de enviar la operacion.

Archivos principales:

```text
src/main/resources/static/index.html
src/main/resources/static/app.js
src/main/resources/static/styles.css
```


## Perfil administrador y usuarios

Para un entorno de pruebas, la recomendacion aplicada es usar una tabla `usuarios` dentro de la misma base de datos de la aplicacion. Esto permite probar login, roles, gestion de usuarios y reset de contrasena sin depender de un proveedor externo de identidad.

Modelo aplicado:

- `usuarios.username`: identificador de acceso unico.
- `usuarios.password_hash`: contrasena almacenada con BCrypt.
- `usuarios.role`: rol `ADMIN` o `USER`.
- `usuarios.estado`: estado operativo del usuario.
- `usuarios.cliente_id`: cliente asociado para usuarios finales; el admin puede quedar sin productos propios.

Al iniciar la aplicacion se crea automaticamente el usuario demo configurado por `app.default.admin` y `app.default.admin-pass`. Por defecto:

```text
usuario: user
password: admin
rol: ADMIN
```

La pantalla de login incluye **Olvide mi contrasena** para reiniciar una contrasena en entorno de pruebas. El flujo usa `POST /api/auth/forgot-password` y devuelve mensajes controlados si el usuario no existe. En produccion esto debe reemplazarse por un flujo con token temporal, correo/SMS o proveedor de identidad.

El modulo **Administrador** permite:

- Crear usuarios operativos o administradores.
- Listar usuarios sin exponer hashes de contrasena.
- Resetear contrasenas por ID de usuario.
- Generar productos bancarios y asociarlos a clientes.
- Consultar varios productos bancarios asociados a un mismo cliente.

El usuario con rol `ADMIN` no posee productos bancarios en su propio portafolio; su funcion es gestionar productos de datos/cuentas para clientes.

Numeracion generada por backend:

```text
AHORROS         -> AHO- + 7 digitos aleatorios
TARJETA_CREDITO -> TC-  + 16 digitos aleatorios
MONETARIA       -> MON- + 10 a 12 digitos aleatorios
```

La generacion verifica que el numero no exista antes de guardar el producto.

## Reportes funcionales y observabilidad

El proyecto separa dos necesidades:

- Reporteria funcional: endpoints `/api/reportes/cartera` y `/api/reportes/operativo`, visibles desde el modulo **Reportes** del frontend. Sirven para entender registros, cartera, movimientos, montos y catalogo de APIs/metodos.
- Observabilidad tecnica: `/api/reportes/tecnico`, `/actuator/health` y logs de Spring Boot. Sirven para verificar disponibilidad y diagnostico tecnico del servicio.

El archivo `TODO.md` mantiene el cierre pendiente del sistema y marca las mejoras ya implementadas.

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


## Presentacion cloud: Netlify + Render + Supabase

El proyecto incluye configuracion para presentar el sistema con frontend y backend separados:

- `netlify.toml`: publica el frontend estatico en Netlify y proxya `/api/*` hacia Render.
- `render.yaml`: define el backend Spring Boot como servicio Docker en Render.
- `.github/workflows/proyecto-final-netlify-render.yml`: verifica Maven, despliega frontend a Netlify y dispara deploy del backend en Render.
- `docs/deploy-netlify-render-supabase.md`: guia de variables, secrets, checklist de despliegue y validacion.

La base recomendada para Render es Supabase PostgreSQL. SQLite no se recomienda para este despliegue porque el servicio web no debe depender de un archivo local para persistencia de clientes, usuarios y transacciones.
## Endpoints principales

Autenticacion:

```text
POST /api/auth/login
POST /api/auth/forgot-password
```


Administracion:

```text
GET  /api/admin/usuarios
POST /api/admin/usuarios
POST /api/admin/usuarios/{id}/cliente/{clienteId}
POST /api/admin/usuarios/{id}/reset-password
POST /api/admin/productos
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
GET  /api/cuentas/numero/{numero}                       # ADMIN
GET  /api/cuentas/numero/{numero}/destinatario          # USER/ADMIN para confirmar beneficiario
GET  /api/cuentas/mis-productos                         # productos del usuario autenticado
GET  /api/cuentas/cliente/{clienteId}                    # ADMIN
POST /api/cuentas/numero/{numero}/cliente/{clienteId}    # ADMIN
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
GET /api/reportes/operativo
GET /api/reportes/tecnico
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
