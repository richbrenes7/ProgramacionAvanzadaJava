# Endpoints - RBrenes Bank

Todos los endpoints internos, excepto autenticacion, Swagger, recursos estaticos y health check, requieren JWT.

## Autenticacion

- `POST /api/auth/login`: autentica usuario y retorna JWT, username y rol.`r`n- `POST /api/auth/forgot-password`: reset demo de contrasena desde el login para entorno de pruebas.

## Administracion

Requiere rol `ADMIN`.

- `GET /api/admin/usuarios`: lista usuarios sin exponer hashes de contrasena.
- `POST /api/admin/usuarios`: crea usuario con rol `USER` o `ADMIN`.
- `POST /api/admin/usuarios/{id}/reset-password`: cambia la contrasena de un usuario.
- `POST /api/admin/productos`: genera y asocia un producto bancario a un cliente.

Numeracion de productos generada por backend:

- Ahorros: `AHO-` + 7 digitos aleatorios.
- Tarjeta de credito: `TC-` + 16 digitos aleatorios.
- Cuenta monetaria: `MON-` + 10 a 12 digitos aleatorios.

## Clientes

- `POST /api/clientes`: crear cliente.
- `GET /api/clientes`: listar clientes.
- `GET /api/clientes/{id}`: consultar cliente.
- `PUT /api/clientes/{id}`: actualizar cliente.

## Cuentas y productos

- `POST /api/cuentas`: crear cuenta; si `numeroCuenta` viene vacio o `AUTO`, el backend genera numeracion aleatoria.
- `GET /api/cuentas/numero/{numero}`: consultar cuenta por numero.
- `GET /api/cuentas/cliente/{clienteId}`: listar cuentas asignadas a un cliente.
- `POST /api/cuentas/numero/{numero}/cliente/{clienteId}`: asociar cuenta existente a cliente.
- `GET /api/cuentas/{numero}/saldo`: consultar saldo.
- `GET /api/cuentas/{id}/movimientos`: consultar movimientos por cuenta.
- `POST /api/cuentas/transferir`: transferir entre cuentas con params `origen`, `destino`, `monto`.

## Movimientos y transacciones

- `POST /api/movimientos`: registrar deposito o retiro.
- `GET /api/movimientos/cuenta/{cuentaId}`: consultar movimientos por cuenta.
- `POST /api/transacciones`: registrar transferencia individual.
- `POST /api/transacciones/lote`: procesar lote concurrente de transacciones.

## Reportes y operacion

- `GET /api/reportes/cartera`: reporte general de cartera.
- `GET /actuator/health`: estado tecnico.
- `GET /swagger-ui/index.html`: documentacion API Swagger/OpenAPI.
