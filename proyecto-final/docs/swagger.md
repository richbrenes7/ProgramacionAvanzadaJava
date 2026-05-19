# Documentación OpenAPI / Swagger UI

La API genera la documentación OpenAPI automáticamente. Accede a la UI localmente en:

```
http://localhost:8080/swagger-ui/index.html
```

Notas:

- El título y la descripción están en español.
- Para incluir ejemplos y descripciones más ricas, anota tus controladores y DTOs con `@Operation`, `@Schema` y Javadoc.
- Puedes exportar el JSON OpenAPI desde `http://localhost:8080/v3/api-docs`.

Si deseas que la UI muestre el logo del banco, copia `docs/images/logo.png` a `src/main/resources/static/images/logo.png` y personaliza la plantilla Swagger UI si es necesario.
