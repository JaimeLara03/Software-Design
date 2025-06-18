@echo off
REM Compila e INSTALA los tres microservicios Spring Boot.
REM Ejecuta este script desde la raíz del repositorio.

REM Compilar e instalar users-service
cd users-service
echo Compilando e instalando users-service...
call .\mvnw.cmd clean install -DskipTests
cd ..

REM Compilar e instalar circuits-service
cd circuits-service
echo Compilando e instalando circuits-service...
call .\mvnw.cmd clean install -DskipTests
cd ..

REM Compilar e instalar payments-service
cd payments-service
echo Compilando e instalando payments-service...
call .\mvnw.cmd clean install -DskipTests
cd ..

REM Instalar dependencias frontend
cd frontend
echo Instalando dependencias frontend...
call npm.cmd install
cd ..

echo ---------------------------------------
echo Compilacion finalizada. Los JAR se encuentran en cada carpeta target y en tu repositorio local de Maven.