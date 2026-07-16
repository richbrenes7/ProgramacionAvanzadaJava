# Despliegue Netlify + Render + Supabase

## Objetivo

Presentar RBrenes Bank con tres piezas separadas:

- **Netlify**: frontend estatico ubicado en `proyecto-final/src/main/resources/static`.
- **Render**: backend Spring Boot empaquetado con Docker desde `proyecto-final/Dockerfile`.
- **Supabase**: base de datos PostgreSQL administrada.

## Por que no SQLite en Render

SQLite puede ejecutarse como archivo local, pero no es la opcion recomendada para Render en este proyecto porque el filesystem del servicio web no debe asumirse como almacenamiento persistente. Aunque Render permite agregar discos persistentes en ciertos planes, para una app bancaria academica con JPA, usuarios, clientes y transacciones conviene usar PostgreSQL. Supabase ya ofrece PostgreSQL administrado, por lo que encaja mejor con Spring Data JPA y con el driver PostgreSQL incluido en el proyecto.

## Archivos agregados

```text
render.yaml
netlify.toml
.github/workflows/proyecto-final-netlify-render.yml
proyecto-final/src/main/resources/application.yml
```

## Supabase

Crear un proyecto en Supabase y obtener los datos de conexion PostgreSQL.

Para Render se recomienda usar una URL JDBC con SSL:

```text
SPRING_DATASOURCE_URL=jdbc:postgresql://<host-supabase>:5432/postgres?sslmode=require
SPRING_DATASOURCE_USERNAME=<usuario-db>
SPRING_DATASOURCE_PASSWORD=<password-db>
```

Para Render/Spring Boot se recomienda conexion directa si esta disponible. Si Render no puede conectarse por IPv6 al host directo de Supabase, usar el **Session pooler** en puerto `5432`. Evitar el **Transaction pooler** en puerto `6543` para JPA/Hibernate, porque ese modo puede chocar con prepared statements.

## Render

El archivo `render.yaml` define el servicio web:

```text
service: rbrenes-bank-api
type: web
runtime: docker
rootDir: proyecto-final
healthCheckPath: /actuator/health
```

Variables que deben completarse como secretos en Render:

```text
SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD
APP_JWT_SECRET
```

Variables ya sugeridas por el blueprint:

```text
APP_KAFKA_LISTENER_AUTO_STARTUP=false
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=false
```

Kafka queda desactivado para Render porque el stack de presentacion no levanta broker Kafka administrado. La funcionalidad concurrente y Kafka queda validada por pruebas locales/CI.

## Netlify

El archivo `netlify.toml` publica el frontend estatico y proxya las rutas `/api/*` al backend Render:

```text
publish = proyecto-final/src/main/resources/static
/api/*  -> https://rbrenes-bank-api.onrender.com/api/:splat
```

Si el servicio de Render cambia de nombre o dominio, actualizar las reglas `to` en `netlify.toml`.

## GitHub Actions

El workflow `.github/workflows/proyecto-final-netlify-render.yml` hace:

1. Verificacion Maven del backend.
2. Despliegue del frontend a Netlify.
3. Trigger de deploy del backend en Render mediante deploy hook.

Secrets requeridos en GitHub:

```text
NETLIFY_AUTH_TOKEN
NETLIFY_SITE_ID
RENDER_DEPLOY_HOOK_URL
```

El deploy de Render usa las variables configuradas en Render, no en GitHub.


## Checklist de despliegue

1. Crear proyecto en Supabase.
2. Copiar credenciales de conexion PostgreSQL.
3. En Render, crear Blueprint desde `render.yaml` o crear Web Service manual apuntando a `proyecto-final/Dockerfile`.
4. En Render, configurar variables secretas:

```text
SPRING_DATASOURCE_URL=jdbc:postgresql://<host>:5432/postgres?sslmode=require
SPRING_DATASOURCE_USERNAME=<usuario>
SPRING_DATASOURCE_PASSWORD=<password>
APP_JWT_SECRET=<cadena-segura-minimo-32-caracteres>
APP_KAFKA_LISTENER_AUTO_STARTUP=false
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=false
```

5. Confirmar que Render responde:

```text
https://rbrenes-bank-api.onrender.com/actuator/health
```

6. En Netlify, crear sitio desde el repositorio o usar GitHub Actions.
7. Confirmar que `netlify.toml` apunta al dominio real de Render. Si el servicio no se llama `rbrenes-bank-api`, reemplazar `https://rbrenes-bank-api.onrender.com` por la URL asignada.
8. En GitHub, si se usara el workflow de despliegue, configurar estos secrets:

```text
NETLIFY_AUTH_TOKEN
NETLIFY_SITE_ID
RENDER_DEPLOY_HOOK_URL
```

9. Entrar al frontend Netlify y probar login:

```text
usuario: user
password: admin
```

10. Crear clientes/productos desde el modulo Administrador para poblar la base Supabase.

## Referencias oficiales usadas

- Render Blueprint: `render.yaml` debe vivir en la raiz del repositorio y define servicios; `runtime: docker` es el runtime recomendado para construir desde Dockerfile.
- Render health checks: `healthCheckPath` permite validar `/actuator/health`.
- Netlify rewrites/proxies: redirects con status `200` permiten que `/api/*` sea proxy hacia otro origen sin cambiar `app.js`.
- Supabase Postgres: usar URL PostgreSQL/JDBC con SSL para backends; para Render conviene conexion directa o Session pooler cuando haya limitaciones de red.

## URLs esperadas

```text
Frontend Netlify: https://<sitio-netlify>.netlify.app
Backend Render:   https://rbrenes-bank-api.onrender.com
Health Render:    https://rbrenes-bank-api.onrender.com/actuator/health
Swagger Render:   https://rbrenes-bank-api.onrender.com/swagger-ui/index.html
```

## Credenciales demo

Al iniciar el backend se crea el administrador demo:

```text
usuario: user
password: admin
rol: ADMIN
```

## Validacion rapida

```bash
curl https://rbrenes-bank-api.onrender.com/actuator/health
```

```bash
curl -X POST https://rbrenes-bank-api.onrender.com/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"admin"}'
```