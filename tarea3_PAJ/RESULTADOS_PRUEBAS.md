# Resultados de pruebas - Tarea 3

## Carpeta objetivo

`D:\Data\tarea3_PAJ`

## Estado final

Build de pruebas: **exitoso**

`mvn test` ejecuto 21 pruebas. El resultado final fue:

- Pruebas ejecutadas: 21
- Fallas: 0
- Errores: 0
- Omitidas: 0

## Correccion aplicada

Se corrigio `ClienteControllerTest`, que fallaba con HTTP 403 en las operaciones `POST` y `PUT`.

El test usa `@WebMvcTest` para validar el controlador con `ClienteService` mockeado. Se agrego:

```java
@AutoConfigureMockMvc(addFilters = false)
```

Esto mantiene la prueba enfocada en el comportamiento del controlador y evita que los filtros de Spring Security bloqueen las peticiones del slice MVC.

Archivo modificado:

`src\\test\\java\\com\\banco\\api\\controller\\ClienteControllerTest.java`

## Cobertura

JaCoCo se genero correctamente con `mvn jacoco:report`.

Resumen principal:

- Instrucciones: 85.22%
- Lineas: 86.05%
- Ramas: 54.55%
- Metodos: 83.72%
- Clases: 100%

Reporte HTML:

`target\\site\\jacoco\\index.html`

## Comandos ejecutados

```powershell
mvn -Dtest=ClienteControllerTest test
mvn test
mvn jacoco:report
```

## Logs generados

```text
logs\\mvn-test-cliente-controller-20260514-234600.log
logs\\mvn-test-fixed-20260514-234634.log
logs\\mvn-jacoco-fixed-20260514-234710.log
logs\\surefire-reports-fixed-20260514-234725\\
logs\\jacoco-fixed-20260514-234725.xml
```

## Pendientes

No quedan bloqueadores de pruebas al cierre de esta ejecucion.
