# Programacion Avanzada en Java

Repositorio del proyecto final de **Programacion Avanzada en Java**.

Este repositorio queda concentrado en un unico entregable: [`proyecto-final/`](proyecto-final/). La decision de dejar solo este proyecto busca que la revision sea directa, que la estructura no mezcle ejercicios previos con el entregable final y que cualquier contributor pueda ubicar rapidamente el codigo, la documentacion, las pruebas y los archivos de ejecucion.

## Proyecto Final - RBrenes Bank

**RBrenes Bank** es una plataforma bancaria desarrollada con Spring Boot y un frontend estatico modular. Integra autenticacion JWT, operaciones bancarias, persistencia JPA, mensajeria Kafka, procesamiento concurrente y pruebas de integracion.

Funciones principales:

- Inicio y cierre de sesion con JWT.
- API REST para clientes, cuentas, movimientos, transacciones y reportes.
- Frontend por modulos: menu principal, clientes, cuentas, saldo, depositos, retiros, transferencias, lotes, movimientos y reportes.
- Restriccion de operaciones de registro/modificacion a usuarios autenticados.
- Persistencia con Spring Data JPA.
- Ejecucion local con H2 para pruebas rapidas sin contenedores.
- Ejecucion con PostgreSQL y Kafka mediante Docker Compose.
- Procesamiento concurrente de lotes de transacciones.
- Actuator health para monitoreo.
- Swagger/OpenAPI para inspeccion y prueba de endpoints.
- Pruebas automatizadas con Maven.

## Estructura

```text
ProgramacionAvanzadaJava/
|-- README.md
|-- .gitignore
|-- .github/
|   `-- workflows/
|       `-- proyecto-final-ci-cd.yml
`-- proyecto-final/
    |-- README.md
    |-- pom.xml
    |-- run-local-h2.bat
    |-- docker-compose.yml
    |-- Dockerfile
    |-- docs/
    `-- src/
```

## Requisitos

- Java JDK 17 o superior.
- Maven 3.9 o superior.
- Git.
- Docker Desktop opcional para ejecutar PostgreSQL, Kafka o pruebas con Testcontainers.

## Ejecucion Local Para Testeos

Para validar la aplicacion rapidamente en un entorno local sin contenedores:

```bash
cd proyecto-final
run-local-h2.bat
```

Este modo usa H2 en memoria y permite probar el frontend, la autenticacion, los endpoints principales y los flujos bancarios sin levantar PostgreSQL ni Kafka manualmente.

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

## Pruebas Automatizadas

Comando recomendado antes de entregar o subir cambios:

```bash
cd proyecto-final
mvn -B verify
```

Estado validado:

```text
Tests run: 7, Failures: 0, Errors: 0, Skipped: 1
BUILD SUCCESS
```

La prueba omitida depende de Docker/Testcontainers cuando Docker no esta disponible. Por eso la ejecucion local con H2 es util para pruebas funcionales rapidas y `mvn verify` queda como validacion automatizada del proyecto.

## Ejecucion Con Contenedores

Para levantar la aplicacion con servicios externos:

```bash
cd proyecto-final
docker compose up -d --build
```

Este modo usa la configuracion de Docker Compose del proyecto final para ejecutar la aplicacion junto con los servicios necesarios de infraestructura.

## Workflow CI/CD

El repositorio incluye el workflow [`proyecto-final-ci-cd.yml`](.github/workflows/proyecto-final-ci-cd.yml), ejecutado por GitHub Actions en `push` y `pull_request` hacia `main` o `master`.

Flujo principal:

1. **Build and Test**: descarga el codigo, configura JDK 17 con Temurin, cachea dependencias Maven y ejecuta `mvn -B verify` dentro de `proyecto-final`.
2. **Upload test reports**: publica reportes de Surefire y Failsafe como artifacts para revisar resultados desde GitHub Actions.
3. **Build Docker Image**: construye la imagen Docker del proyecto final usando el `Dockerfile` incluido.
4. **Optional Deploy to AWS**: si la variable del repositorio `CLOUD_TARGET` vale `aws`, el workflow autentica con AWS, construye la imagen y la publica en Amazon ECR.
5. **Optional Deploy to Azure**: si `CLOUD_TARGET` vale `azure`, el workflow autentica con Azure y despliega el `.jar` en Azure Web App.

La asociacion con nube queda preparada de forma controlada por variables y secretos. No se suben credenciales al repositorio; GitHub Actions toma los valores desde la configuracion segura del repo.

Variables y secretos esperados para AWS:

```text
vars.CLOUD_TARGET=aws
vars.AWS_REGION
vars.AWS_ECR_REPOSITORY
secrets.AWS_ROLE_TO_ASSUME
```

Variables y secretos esperados para Azure:

```text
vars.CLOUD_TARGET=azure
vars.AZURE_WEBAPP_NAME
secrets.AZURE_CREDENTIALS
```

Si `CLOUD_TARGET` no esta configurado, el workflow se limita a compilar, probar y construir la imagen Docker.

## Documentacion

- [`proyecto-final/README.md`](proyecto-final/README.md): guia completa del proyecto final.
- [`proyecto-final/docs/`](proyecto-final/docs/): arquitectura, endpoints, Swagger, evidencias, branding y diagramas.

## Mantenimiento

- Mantener el proyecto final como unico entregable visible del repositorio.
- Ejecutar `mvn -B verify` antes de hacer push.
- Usar `run-local-h2.bat` para pruebas funcionales rapidas.
- No subir secretos reales al repositorio.
- No versionar artefactos generados por Maven (`target/`).
