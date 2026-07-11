# TODO - Cierre del sistema RBrenes Bank

Este listado concentra lo pendiente para dejar el proyecto final como sistema academico completo y defendible.

## Prioridad alta

- [x] Separar reportes funcionales de observabilidad tecnica.
- [x] Ampliar `/api/reportes/cartera` con distribucion de productos, estados y saldos por tipo.
- [x] Agregar `/api/reportes/operativo` para registros, movimientos recientes y catalogo de APIs/metodos.
- [x] Mostrar reportes operativos en el frontend con metricas, listas y catalogo de endpoints.
- [x] Persistir formalmente la relacion usuario-cliente-producto para que el usuario solo vea sus productos desde backend.
- [x] Agregar pruebas de integracion para admin, reportes y permisos por rol.

## Prioridad media

- [x] Crear endpoint tecnico/documental que explique health, logs y estado de APIs sin mezclarlo con reportes financieros.
- [x] Documentar los reportes en README y docs/endpoints.md.
- [ ] Agregar exportacion simple de reportes a JSON o CSV desde frontend.
- [x] Mejorar mensajes de error para escenarios sin datos o sin permisos.

## Prioridad baja

- [ ] Sustituir reset demo de contrasena por token temporal si el alcance deja de ser academico.
- [ ] Agregar filtros por fechas para movimientos y reportes.
- [ ] Agregar capturas/evidencias de frontend para la entrega final.

## Notas de alcance

- `/actuator/health` y logs pertenecen a observabilidad tecnica.
- `/api/reportes/*` pertenece a reporteria funcional: cartera, registros, movimientos y catalogo operativo.
- El rol `ADMIN` gestiona usuarios y productos de clientes; no posee productos bancarios propios.
