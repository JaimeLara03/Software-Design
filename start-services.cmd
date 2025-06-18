@echo off
REM Lanza los microservicios Spring Boot en ventanas independientes

REM Users-service en puerto 8081
start "users" cmd /k "cd users-service && .\mvnw.cmd spring-boot:run"

REM Circuits-service en puerto 8082
start "circuits" cmd /k "cd circuits-service && .\mvnw.cmd spring-boot:run"

REM Payments-service en puerto 8083
start "payments" cmd /k "cd payments-service && .\mvnw.cmd spring-boot:run"

REM Frontend en puerto 4200 (por defecto Angular)
start "frontend" cmd /k "cd frontend && npm.cmd start"

echo ---
echo Script lanzado. Se han abierto tres ventanas.
echo Cierra cada ventana o pulsa Ctrl+C dentro de ella para detener el servicio correspondiente.
