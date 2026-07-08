# Documentacion de `proyecto-final`

## Autoria

- Autor: Rich Brenes
- GitHub: [`richbrenes7`](https://github.com/richbrenes7)
- Proyecto: RBrenes Bank

## Diagramas UML y maquetacion

Los archivos editables se encuentran en `docs/diagrams`:

- `casos-uso-proyecto-final.puml`: casos de uso del banco, banca en linea y administracion.
- `clases-dominio-proyecto-final.puml`: clases principales del dominio bancario.
- `secuencia-asignacion-cuenta.puml`: flujo para asignar una cuenta existente a un cliente.
- `actividad-flujo-front.puml`: navegacion del frontend desde pagina publica hasta modulos privados.
- `maquetacion-banca-linea.puml`: wireframe en PlantUML/Salt para generar imagen rapida.
- `maquetacion-banca-linea.drawio`: maquetacion editable en Draw.io / diagrams.net.
- `componentes.puml`, `deployment-docker-compose.puml`, `concurrencia-transacciones.puml` y `sequence-transferencia.puml`: arquitectura, despliegue, concurrencia y transferencia.

Las imagenes PNG generadas estan en `docs/images/diagrams`:

- `casos-uso-proyecto-final.png`
- `clases-dominio-proyecto-final.png`
- `secuencia-asignacion-cuenta.png`
- `actividad-flujo-front.png`
- `maquetacion-banca-linea.png`
- Imagenes de componentes, Docker Compose, concurrencia, transferencia y diagramas heredados.

Para regenerar imagenes PNG localmente, coloca `plantuml.jar` en la raiz del proyecto y ejecuta:

```bat
cd proyecto-final
docs\generate-diagrams.bat
```

El script genera PNGs en `docs/images/diagrams`.

## Imagenes de marca

Las imagenes de marca estan en:

```text
docs/images/RB_logo_sin_fondo.png
docs/images/RBrenes_Bank_sin_fondo.png
src/main/resources/static/assets/RB_logo_sin_fondo.png
src/main/resources/static/assets/RBrenes_Bank_sin_fondo.png
```

## Arquitectura

Ver [architecture.md](architecture.md) para la descripcion general.