# Tarea 3 - API REST bancaria

Proyecto Spring Boot para clientes, cuentas y movimientos bancarios con autenticacion JWT, Swagger/OpenAPI, manejo uniforme de errores y pruebas automatizadas.

## Objetivo

Construir una API REST bancaria con Spring Boot que permita autenticarse, administrar clientes, consultar saldos y consultar movimientos con filtros y paginacion.

## Incluye

- JWT para autenticacion.
- CRUD de clientes.
- Consulta de saldo por cuenta.
- Consulta de movimientos con filtros y paginacion.
- Manejo centralizado de errores con `ProblemDetail`.
- Swagger UI con OpenAPI.
- Pruebas unitarias y reporte JaCoCo.
- Diagramas UML, imagenes PNG y maquetacion editable Draw.io.

## Documentacion visual

Fuentes editables:

```text
docs/diagrams/casos-uso-tarea3.puml
docs/diagrams/clases-tarea3.puml
docs/diagrams/componentes-tarea3.puml
docs/diagrams/secuencia-consulta-saldo-tarea3.puml
docs/diagrams/maquetacion-swagger-tarea3.puml
docs/diagrams/maquetacion-api-tarea3.drawio
```

Imagenes generadas:

```text
docs/images/diagrams/casos-uso-tarea3.png
docs/images/diagrams/clases-tarea3.png
docs/images/diagrams/componentes-tarea3.png
docs/images/diagrams/secuencia-consulta-saldo-tarea3.png
docs/images/diagrams/maquetacion-swagger-tarea3.png
```

## Estructura

```text
tarea-3-api-rest/
|-- pom.xml
|-- README.md
|-- requests/
|   `-- api.http
|-- docs/
|   |-- diagrams/
|   `-- images/diagrams/
`-- src/
    |-- main/java/com/banco/api/
    `-- test/java/com/banco/api/
```

## Variables de entorno

- `SERVER_PORT`
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET`
- `JWT_EXPIRATION_MINUTES`
- `APP_AUTH_USERNAME`
- `APP_AUTH_PASSWORD`

## Ejecutar

```bash
mvn test
mvn spring-boot:run
```

## Swagger

- UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Buenas practicas

- No escribir secretos en el codigo.
- Separar DTOs de entidades.
- Validar entradas con Jakarta Validation.
- Responder errores con formato consistente.
