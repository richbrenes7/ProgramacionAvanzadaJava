# ProgramacionAvanzadaJava

Repositorio central del curso de Programación Avanzada en Java.

## Contenido

- `tarea-3-api-rest/` — API REST bancaria con Spring Boot, JWT, validaciones, pruebas y Swagger.
- `tarea-4-concurrencia-kafka/` — Procesamiento concurrente de transacciones con `ExecutorService` y Kafka.
- `proyecto-final/` — Plataforma bancaria Java integrando seguridad, concurrencia, persistencia y despliegue.
- `docs/` — Buenas prácticas, convenciones y lineamientos comunes.

## Principios del repositorio

- Configuración por variables de entorno.
- Separación por módulos o entregables.
- Documentación mínima por cada entrega.
- Pruebas y cobertura como parte del flujo normal.
- Secretos y credenciales fuera del código fuente.

## Convenciones

- Usa nombres de paquetes en minúsculas.
- Mantén capas separadas: `controller`, `service`, `repository`, `dto`, `entity`, `security`, `exception`.
- Documenta endpoints y parámetros en `README.md` o archivos dentro de `docs/`.
- Evita valores fijos para URLs, claves y tiempos de expiración.

## Variables de entorno

Consulta [`.env.example`](.env.example) para ver las variables sugeridas.

## Próximos pasos

1. Completar la documentación de cada tarea.
2. Agregar la implementación de la tarea 3.
3. Agregar el módulo concurrente de la tarea 4.
4. Integrar el proyecto final en una estructura lista para entrega.
