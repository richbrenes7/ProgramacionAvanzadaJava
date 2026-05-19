# JavaBank Online — Proyecto Final (es)

Proyecto monolito modular para la entrega final del curso. Incluye API REST para clientes, cuentas, movimientos, transferencias y seguridad JWT.

Documentación y recursos:

```bash
cd proyecto-final
docker compose up -d
mvn -B test
mvn spring-boot:run
```

Comandos rápidos:

```bash
cd proyecto-final
docker compose up -d
mvn -B test
mvn spring-boot:run
```

Swagger UI: http://localhost:8080/swagger-ui/index.html

# Proyecto final — Banco Digital Core API

## Objetivo

Construir una plataforma bancaria Java con Spring Boot, seguridad JWT, persistencia, concurrencia, reportes, Docker y pruebas automatizadas.

## Módulos sugeridos

- Clientes
- Cuentas
- Movimientos
- Transacciones concurrentes
- Seguridad
- Reportes

## Variables de entorno

- `POSTGRES_HOST`
- `POSTGRES_PORT`
- `POSTGRES_DB`
- `POSTGRES_USER`
- `POSTGRES_PASSWORD`
- `JWT_SECRET`
- `JWT_EXPIRATION_MINUTES`
- `LOG_LEVEL`

## Arquitectura recomendada

- Separar el monolito por dominios o crear servicios modulares.
- Mantener configuraciones por perfil.
- Usar OpenAPI para documentar endpoints.
- Definir pruebas unitarias e integración desde el inicio.

## Entregables

- `README.md`
- `docker-compose.yml`
- `.env.example`
- `docs/`
- Código fuente con pruebas
