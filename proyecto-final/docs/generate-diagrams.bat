@echo off
REM Script para generar PNGs desde PlantUML (requiere plantuml.jar y java en PATH)
REM Uso: coloca plantuml.jar en el mismo directorio o en %PATH% y ejecuta este script desde la raíz del proyecto
if not exist plantuml.jar (
  echo plantuml.jar no encontrado en el directorio actual. Descarga desde https://plantuml.com/es/download
  exit /b 1
)
if not exist docs\images\diagrams mkdir docs\images\diagrams
java -jar plantuml.jar -tpng -o ..\images\diagrams docs\diagrams\*.puml
if %ERRORLEVEL% neq 0 (
  echo Error generando diagramas
  exit /b %ERRORLEVEL%
)
echo Diagramas generados en docs\images\diagrams
