# Tarea 3 — API REST bancaria

Proyecto Spring Boot para la tarea 3 del curso.

## Incluye

- JWT para autenticación.
- CRUD de clientes.
- Consulta de saldo por cuenta.
- Consulta de movimientos con filtros y paginación.
- Manejo centralizado de errores con `ProblemDetail`.
- Swagger UI con OpenAPI.
- Pruebas unitarias y reporte JaCoCo.

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
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`# Tarea 3 — API REST bancaria

## Objetivo

Construir una API REST con Spring Boot para clientes, cuentas y movimientos, con JWT, validación, manejo centralizado de errores, Swagger y pruebas.

## Alcance sugerido

- Login con JWT.
- CRUD de clientes.
- Consulta de saldo por cuenta.
- Consulta de movimientos con filtros de fecha y paginación.
- Manejo de errores con respuesta uniforme.
- Cobertura mínima objetivo: 80%.

## Variables de entorno

- `SERVER_PORT`
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET`
- `JWT_EXPIRATION_MINUTES`

## Buenas prácticas

- No escribir secretos en el código.
- Separar DTOs de entidades.
- Validar entradas con Jakarta Validation.
- Responder errores con formato consistente.

## Documentación

- Agregar colección HTTP o Postman en `requests/`.
- Documentar el uso de Swagger UI.
