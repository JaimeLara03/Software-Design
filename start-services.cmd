@echo off
REM Lanza los microservicios Spring Boot en ventanas independientes

REM ---------------- Configuración Maven ----------------
REM La variable de entorno MAVEN_HOME debe apuntar a la carpeta donde está instalado Maven
if not defined MAVEN_HOME (
  echo Por favor defina la variable de entorno MAVEN_HOME con la ruta de Maven. >&2
  exit /b 1
)
set "MVN=%MAVEN_HOME%\bin\mvn.cmd"
REM -----------------------------------------------------

REM Users-service en puerto 8081
start "users" cmd /k "cd users-service\target && java -jar users-service-0.0.1-SNAPSHOT.jar"

REM Circuits-service en puerto 8082
start "circuits" cmd /k "cd circuits-service && "%MVN%" spring-boot:run"

REM Payments-service en puerto 8083
start "payments" cmd /k "cd payments-service && "%MVN%" spring-boot:run"

REM Frontend en puerto 4200 (por defecto Angular)
start "frontend" cmd /k "cd frontend && npm.cmd start"

echo ---
echo Script lanzado. Se han abierto cuatro ventanas.
echo Cierra cada ventana o pulsa Ctrl+C dentro de ella para detener el servicio correspondiente.
