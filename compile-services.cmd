@echo off
REM Compila e INSTALA los tres microservicios Spring Boot.
REM Ejecuta este script desde la raíz del repositorio.

REM ---------------- Configuración Maven ----------------
REM La variable de entorno MAVEN_HOME debe apuntar a la carpeta donde está instalado Maven
if not defined MAVEN_HOME (
  echo Por favor defina la variable de entorno MAVEN_HOME con la ruta de Maven. >&2
  exit /b 1
)
set "MVN=%MAVEN_HOME%\bin\mvn.cmd"
REM -----------------------------------------------------

REM Compilar e instalar users-service
cd users-service
echo Compilando e instalando users-service...
call "%MVN%" -B clean install -DskipTests
cd ..

REM Compilar e instalar circuits-service
cd circuits-service
echo Compilando e instalando circuits-service...
call "%MVN%" -B clean install -DskipTests
cd ..

REM Compilar e instalar payments-service
cd payments-service
echo Compilando e instalando payments-service...
call "%MVN%" -B clean install -DskipTests
cd ..

REM Instalar dependencias frontend
cd frontend
echo Instalando dependencias frontend...
call npm.cmd install
cd ..

echo ---------------------------------------
echo Compilacion finalizada. Los JAR se encuentran en cada carpeta target y en tu repositorio local de Maven.