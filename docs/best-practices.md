# Buenas prácticas del repositorio

## Configuración

- Todo parámetro sensible debe venir de variables de entorno.
- Mantén un archivo `.env.example` como contrato de configuración.
- No subas secretos reales al repositorio.
- Define perfiles separados para desarrollo, pruebas y producción.

## Estructura recomendada

- `controller` para exponer la API.
- `service` para la lógica de negocio.
- `repository` para acceso a datos.
- `dto` para contratos de entrada y salida.
- `entity` para persistencia.
- `security` para autenticación y autorización.
- `exception` para manejo centralizado de errores.

## API REST

- Usa nombres en plural para los recursos.
- Prefiere códigos HTTP semánticos.
- Devuelve respuestas consistentes.
- Documenta cada endpoint con ejemplos.

## Seguridad

- Protege endpoints con JWT.
- Mantén el secreto JWT fuera del código.
- Evita credenciales hardcodeadas.
- Usa tiempos de expiración explícitos.

## Pruebas

- Agrega pruebas unitarias para la lógica de negocio.
- Agrega pruebas de controlador con `MockMvc`.
- Revisa cobertura antes de entregar.
- Automatiza la validación con Maven.

## Observabilidad

- Usa logs estructurados cuando aplique.
- Registra errores con contexto útil.
- Evita mensajes ambiguos en producción.
