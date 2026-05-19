# Evidencias de ejecución y pruebas

Este documento recoge artefactos y comandos para generar evidencias de ejecución, pruebas y cobertura.

1. Levantar servicios con Docker Compose:

```bash
cd proyecto-final
docker compose up -d
```

2. Ejecutar pruebas y generar JaCoCo HTML:

```bash
mvn -B test jacoco:report
```

3. Ubicación de logs y artefactos:

- Informes JaCoCo: `target/site/jacoco/index.html`
- Logs de la aplicación: contenedor `app` (ver `docker compose logs app`)
- Capturas y screenshots: `docs/images/evidencias/`

4. Incluir imágenes y logos en la entrega (ya están en `docs/images/`).
