# Endpoints sugeridos — JavaBank Online

- POST `/api/auth/login` — Autenticación (retorna JWT)
- POST `/api/clientes` — Crear cliente
- GET `/api/clientes/{id}` — Consultar cliente
- POST `/api/cuentas` — Crear cuenta
- GET `/api/cuentas/{numero}/saldo` — Consultar saldo
- GET `/api/cuentas/{id}/movimientos` — Consultar movimientos por cuenta
- POST `/api/transacciones` — Registrar una transacción sencilla
- POST `/api/transacciones/lote` — Procesar lote concurrente de transacciones
- POST `/api/cuentas/transferir` — Transferir entre cuentas (params: origen,destino,monto)
- GET `/api/reportes/cartera` — Reporte general de cartera
- GET `/actuator/health` — Estado técnico
- GET `/swagger-ui.html` — Documentación API (Swagger/OpenAPI)

Notas:

- Usar DTOs para entrada/salida y validaciones con Bean Validation (`@Valid`).
- Proteger endpoints con JWT; solo `/api/auth/**` y `/swagger-ui/**` deben ser públicos.
