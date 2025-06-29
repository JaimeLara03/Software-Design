@echo off
setlocal
TITLE Limpiador de Repositorio - Version Segura

REM --- Mensaje inicial ---
echo.
echo ======================================================
echo      INICIANDO LIMPIEZA COMPLETA DEL REPOSITORIO
echo ======================================================
echo.
echo Este script solo eliminara archivos y carpetas.
echo.
pause
echo.

REM ---------------- Configuración Maven ----------------
REM La variable de entorno MAVEN_HOME debe apuntar a la carpeta donde está instalado Maven
if not defined MAVEN_HOME (
  echo Por favor defina la variable de entorno MAVEN_HOME con la ruta de Maven. >&2
  pause
  goto :end
)
set "MVN=%MAVEN_HOME%\bin\mvn.cmd"
REM -----------------------------------------------------

REM --- 1. Limpieza del Backend (Todos los modulos Maven) ---
echo [1/3] Limpiando proyectos de backend (Maven)...
echo.

REM Se mueve temporalmente a un submodulo para usar su wrapper
cd circuits-service

REM Llama al wrapper para limpiar el proyecto raiz. Maven mostrara su progreso.
call "%MVN%" -f ..\pom.xml clean
if %errorlevel% neq 0 (
    cd ..
    echo.
    echo ERROR: El comando 'mvnw.cmd clean' fallo.
    goto :error
)

REM Vuelve al directorio raiz
cd ..


REM --- 2. Limpieza del Frontend (Angular/npm) ---
REM Asegurarnos de que no haya procesos Node activos que bloqueen archivos
echo Cerrando procesos Node/Angular que puedan estar en ejecucion...
rem Cerrar procesos Node y sus hijos (usa /T)
for %%p in (node.exe npm.exe npm.cmd ng.cmd ng.exe) do (
    tasklist /FI "IMAGENAME eq %%p" | find /I "%%p" >nul 2>nul
    if not errorlevel 1 (
        echo  - Deteniendo %%p y sus procesos hijos...
        taskkill /F /T /IM %%p >nul 2>nul
    )
)
rem Breve pausa para liberar los handles
ping -n 3 127.0.0.1 >nul 2>nul

echo [2/3] Limpiando proyecto de frontend (Angular)...
echo.
cd frontend

if exist "node_modules" (
    echo  - Eliminando 'node_modules'...
    rmdir /s /q "node_modules"
)
if exist ".angular" (
    echo  - Eliminando cache de Angular '.angular'...
    rmdir /s /q ".angular"
)
if exist "dist" (
    echo  - Eliminando directorio de compilacion 'dist'...
    rmdir /s /q "dist"
)

rem Borrar cache adicional de Angular 16 (carpeta 'angular')
if exist "angular" (
    echo  - Eliminando cache de Angular 'angular'...
    rmdir /s /q "angular"
)

rem Borrar cache dentro de node_modules si existe (Babel, Vite, etc.)
if exist "node_modules\.cache" (
    echo  - Eliminando '.cache' dentro de node_modules...
    rmdir /s /q "node_modules\.cache"
)

echo  - Limpiando cache de npm...
call npm cache clean --force >nul 2>&1

:: Volver al directorio raíz del repositorio
echo.
cd ..

REM --- 3. Eliminación de directorios 'target' restantes ---
echo [3/3] Eliminando directorios 'target' remanentes...
echo.
for /d /r %%d in (target) do (
    if exist "%%d" (
        echo      - Eliminando "%%d"...
        rmdir /s /q "%%d"
    )
)




REM --- Mensaje final ---
echo ======================================================
echo              LIMPIEZA COMPLETADA
echo ======================================================
echo.
echo El repositorio esta ahora mucho mas ligero.
echo.
goto :end

:error
echo.
echo X-- La limpieza fallo. Por favor, revisa los errores. --X
echo.

:end
pause
endlocal