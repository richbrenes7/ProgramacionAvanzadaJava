# Endpoints y metodos REST - RBrenes Bank

## Autoria

- Autor: Rich Brenes
- GitHub: [`richbrenes7`](https://github.com/richbrenes7)
- Proyecto: RBrenes Bank
- Curso: Programacion Avanzada en Java

## Criterio de diseno

La API se organizo por modulos de negocio para separar responsabilidades: autenticacion, administracion, clientes, productos bancarios, movimientos, transacciones y reportes. El frontend consume estas rutas desde modulos separados y el backend aplica seguridad por JWT y rol.

Reglas generales:

- Rutas publicas: recursos estaticos, Swagger, health, favicon y `/api/auth/**`.
- Rutas `ADMIN`: clientes, administracion, reportes, asignaciones y consultas globales.
- Rutas `USER/ADMIN`: operaciones sobre productos propios, transferencias y consulta de beneficiario.
- Los usuarios cliente no consultan IDs de clientes ni tablas globales; operan por producto bancario.

## Autenticacion

| Metodo | Ruta | Acceso | Descripcion | Por que se utiliza |
| --- | --- | --- | --- | --- |
| POST | `/api/auth/login` | Publico | Valida usuario/contrasena y retorna JWT, username, rol y cliente asociado. | Centraliza el inicio de sesion y permite que el frontend opere con autorizacion Bearer sin sesiones de servidor. |
| POST | `/api/auth/forgot-password` | Publico demo | Reinicia contrasena por username en entorno de pruebas. | Permite demostrar recuperacion de acceso sin proveedor externo; en produccion debe reemplazarse por token temporal/correo. |

## Administracion

Todas las rutas requieren rol `ADMIN`.

| Metodo | Ruta | Acceso | Descripcion | Por que se utiliza |
| --- | --- | --- | --- | --- |
| GET | `/api/admin/usuarios` | ADMIN | Lista usuarios sin exponer `password_hash`. | Permite al gestor revisar usuarios creados y administrar accesos sin filtrar credenciales sensibles. |
| POST | `/api/admin/usuarios` | ADMIN | Crea usuario `USER` o `ADMIN`, con `clienteId` opcional. | Separa gestion de identidad del usuario final y permite asociar clientes a usuarios. |
| POST | `/api/admin/usuarios/{id}/cliente/{clienteId}` | ADMIN | Asocia usuario existente a cliente. | Permite corregir o completar la relacion usuario-cliente despues de crear registros. |
| POST | `/api/admin/usuarios/{id}/reset-password` | ADMIN | Cambia contrasena de un usuario por ID. | Soporta mesa de ayuda/administracion interna en pruebas. |
| POST | `/api/admin/productos` | ADMIN | Genera producto bancario y lo asocia a cliente. | Mantiene la creacion de productos en el perfil administrador, no en clientes finales. |

Numeracion generada por backend:

| Producto | Formato | Justificacion |
| --- | --- | --- |
| Ahorros | `AHO-` + 7 digitos aleatorios | Evita secuencias visibles y simplifica cuentas de ahorro demo. |
| Tarjeta de credito | `TC-` + 16 digitos aleatorios | Simula numeracion de tarjeta sin depender de un proveedor real. |
| Monetaria | `MON-` + 10 a 12 digitos aleatorios | Diferencia cuenta monetaria de ahorro y credito. |

## Clientes

Todas las rutas requieren rol `ADMIN`.

| Metodo | Ruta | Acceso | Descripcion | Por que se utiliza |
| --- | --- | --- | --- | --- |
| POST | `/api/clientes` | ADMIN | Crea cliente con datos personales y estado. | Solo administracion debe registrar clientes maestros. |
| GET | `/api/clientes` | ADMIN | Lista clientes. | Alimenta el panel administrativo y evita que usuarios finales vean otros clientes. |
| GET | `/api/clientes/{id}` | ADMIN | Consulta cliente puntual. | Permite validacion administrativa antes de asociar productos o usuarios. |
| PUT | `/api/clientes/{id}` | ADMIN | Actualiza datos de cliente. | Mantiene datos maestros editables sin tocar productos o movimientos. |

## Cuentas y productos bancarios

| Metodo | Ruta | Acceso | Descripcion | Por que se utiliza |
| --- | --- | --- | --- | --- |
| POST | `/api/cuentas` | ADMIN | Crea cuenta/producto; si numero viene vacio o `AUTO`, genera numeracion. | Apertura de producto controlada por administracion. |
| GET | `/api/cuentas/mis-productos` | USER/ADMIN | Lista productos del `clienteId` asociado al usuario autenticado; admin recibe lista vacia como portafolio propio. | Evita que el cliente consulte productos ajenos y confirma que el admin no posee productos bancarios personales. |
| GET | `/api/cuentas/numero/{numero}` | ADMIN | Consulta entidad completa de cuenta por numero. | Se reserva para administracion porque expone datos internos como `clienteId`. |
| GET | `/api/cuentas/numero/{numero}/destinatario` | USER/ADMIN | Devuelve numero, nombre del cliente, tipo y estado de la cuenta destino. | Permite confirmar beneficiario en transferencias sin revelar todos los datos del cliente. |
| GET | `/api/cuentas/cliente/{clienteId}` | ADMIN | Lista productos asociados a un cliente. | Soporta gestion administrativa de varios productos por cliente. |
| POST | `/api/cuentas/numero/{numero}/cliente/{clienteId}` | ADMIN | Asocia cuenta existente a cliente. | Permite corregir/registrar asignaciones desde administracion. |
| GET | `/api/cuentas/{numero}/saldo` | USER/ADMIN | Consulta saldo por numero de cuenta. | Permite mostrar disponibilidad del producto seleccionado. |
| GET | `/api/cuentas/{id}/movimientos` | USER/ADMIN | Consulta movimientos por ID de cuenta. | Complementa historial por producto; para usuarios se valida pertenencia desde servicios/controladores relacionados. |
| POST | `/api/cuentas/transferir?origen=&destino=&monto=` | USER/ADMIN | Transferencia por parametros. | Se mantiene como compatibilidad de API simple; el frontend principal usa `/api/transacciones`. |

## Movimientos

| Metodo | Ruta | Acceso | Descripcion | Por que se utiliza |
| --- | --- | --- | --- | --- |
| POST | `/api/movimientos` | USER/ADMIN | Registra movimiento de cuenta. Usuarios no pueden crear `DEPOSITO`; retiros deben pertenecer a su cuenta. | Modela notas de debito del cliente y evita que el usuario manipule notas de credito/depositos. |
| GET | `/api/movimientos/cuenta/{cuentaId}` | USER/ADMIN | Lista movimientos de una cuenta. | Alimenta historial y graficas de balance/movimientos por producto. |

## Transacciones

| Metodo | Ruta | Acceso | Descripcion | Por que se utiliza |
| --- | --- | --- | --- | --- |
| POST | `/api/transacciones` | USER/ADMIN | Ejecuta transferencia individual entre cuenta origen y destino. | Es el flujo principal para mover fondos; valida que la cuenta origen pertenezca al usuario. |
| POST | `/api/transacciones/lote` | USER/ADMIN | Procesa varias transferencias iguales en lote. | Demuestra concurrencia con procesamiento por lotes y cubre el requisito academico de transacciones concurrentes. |

## Reportes y sistema

Todas las rutas `/api/reportes/**` requieren rol `ADMIN`.

| Metodo | Ruta | Acceso | Descripcion | Por que se utiliza |
| --- | --- | --- | --- | --- |
| GET | `/api/reportes/cartera` | ADMIN | Reporta total de cuentas, clientes con productos, saldo total y agrupaciones por tipo/estado. | Resume cartera bancaria para revision funcional. |
| GET | `/api/reportes/operativo` | ADMIN | Reporta registros, movimientos recientes, montos por tipo y catalogo de APIs/metodos. | Explica actividad del sistema y ayuda a entender los metodos REST usados. |
| GET | `/api/reportes/tecnico` | ADMIN | Reporte documental de health, Swagger y logs. | Separa observabilidad tecnica de reporteria bancaria funcional. |
| GET | `/actuator/health` | Publico | Estado tecnico del servicio. | Permite health check local, Render y monitoreo basico. |
| GET | `/swagger-ui/index.html` | Publico | UI Swagger/OpenAPI. | Facilita exploracion y prueba manual de endpoints REST. |
| GET | `/v3/api-docs/**` | Publico | Especificacion OpenAPI generada. | Alimenta Swagger y herramientas externas de documentacion. |
| GET | `/favicon.ico` | Publico | Devuelve favicon/204 controlado. | Evita errores 403/404 molestos del navegador por favicon. |

## Endpoints consumidos por modulo frontend

| Modulo UI | APIs principales | Justificacion funcional |
| --- | --- | --- |
| Login | `/api/auth/login`, `/api/auth/forgot-password` | Inicio de sesion y recuperacion demo. |
| Administrador | `/api/admin/usuarios`, `/api/admin/productos`, `/api/clientes`, `/api/cuentas/cliente/{clienteId}` | Gestion de usuarios, clientes y productos. |
| Cuentas | `/api/cuentas/mis-productos`, `/api/cuentas/{numero}/saldo` | Portafolio propio del usuario. |
| Transferencias | `/api/cuentas/numero/{numero}/destinatario`, `/api/transacciones` | Confirmar beneficiario y ejecutar debito/credito. |
| Lotes | `/api/transacciones/lote` | Prueba de concurrencia/transacciones masivas. |
| Movimientos | `/api/movimientos/cuenta/{cuentaId}` | Historial y graficas por producto. |
| Reportes | `/api/reportes/cartera` | Revision funcional de cartera. |
| Sistema | `/api/reportes/operativo`, `/api/reportes/tecnico`, `/actuator/health`, Swagger | Diagnostico, catalogo REST y salud del API. |

## Notas de seguridad

- El frontend puede navegar paneles, pero las operaciones protegidas dependen del JWT.
- Las rutas de clientes, asignaciones y reportes no son visibles ni accesibles para usuarios `USER`.
- Las transferencias de usuarios validan que la cuenta origen pertenezca al `clienteId` asociado al usuario autenticado.
- Los depositos/notas de credito no son manipulables por usuarios finales; se originan por transferencias recibidas u operaciones administrativas.