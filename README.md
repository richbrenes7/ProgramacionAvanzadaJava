# Programacion Avanzada en Java

Repositorio del proyecto final de **Programacion Avanzada en Java**.

El contenido principal esta en [`proyecto-final/`](proyecto-final/): una plataforma bancaria desarrollada con Spring Boot, frontend estatico modular, seguridad JWT, persistencia JPA, mensajeria Kafka y pruebas de integracion.

## Estructura

```text
ProgramacionAvanzadaJava/
|-- README.md
|-- .gitignore
`-- proyecto-final/
    |-- README.md
    |-- pom.xml
    |-- run-local-h2.bat
    |-- docker-compose.yml
    |-- docs/
    `-- src/
```

## Proyecto final - RBrenes Bank

Incluye:

- Inicio y cierre de sesion con JWT.
- API REST para clientes, cuentas, movimientos, transacciones y reportes.
- Frontend separado por modulos: menu principal, clientes, cuentas, saldo, depositos, retiros, transferencias, lotes, movimientos y reportes.
- Persistencia con Spring Data JPA.
- Ejecucion local con H2 en memoria.
- Ejecucion con PostgreSQL y Kafka mediante Docker Compose.
- Procesamiento concurrente de lotes de transacciones.
- Actuator health.
- Swagger/OpenAPI.
- Pruebas de integracion.

## Ejecucion local sin contenedores

```bash
cd proyecto-final
run-local-h2.bat
```

URLs principales:

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

## Pruebas

```bash
cd proyecto-final
mvn -B verify
```

Estado validado:

```text
Tests run: 7, Failures: 0, Errors: 0, Skipped: 1
BUILD SUCCESS
```

La prueba omitida depende de Docker/Testcontainers cuando Docker no esta disponible.

## Ejecucion con contenedores

```bash
cd proyecto-final
docker compose up -d --build
```

## Documentacion

- [`proyecto-final/README.md`](proyecto-final/README.md): guia completa del proyecto.
- [`proyecto-final/docs/`](proyecto-final/docs/): arquitectura, endpoints, Swagger, evidencias y diagramas.

## Mantenimiento

- No subir secretos reales al repositorio.
- No versionar artefactos generados por Maven (`target/`).
- Mantener el proyecto final como unico entregable visible del repositorio.
