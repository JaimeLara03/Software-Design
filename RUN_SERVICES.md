# Guía para Lanzar el Proyecto (Back-end + Front-end)

Esta guía resume los pasos necesarios para compilar y ejecutar los **tres microservicios Spring Boot** y el **front-end Angular** en un entorno Windows.

---
## 1. Requisitos Previos

| Componente | Versión mínima | Estado |
|------------|----------------|--------|
| JDK        | 17             | ✅ (Java 21 detectado) |
| Node.js    | 18 LTS o 20    | _(verifica con `node -v`) |
| Angular CLI| ^16            | Instala con `npm i -g @angular/cli` |
| SQL Server | Express o Dev  | ✅ Instancia local con `users_db` |
| MySQL      | 8.x            | ✅/❌ según tu instalación |

> Añade los ejecutables (`mysql.exe`, etc.) al **PATH** si no los reconoce la consola.

---
## 2. Compilar los Microservicios

Cada carpeta (`users-service`, `circuits-service`, `payments-service`) incluye el wrapper **Maven** (`mvnw.cmd`), por lo que **no necesitas instalar Maven**.

```powershell
# Ejecutar una sola vez o tras cambios en el código
cd users-service      ; .\mvnw.cmd clean package -DskipTests
cd ..\circuits-service; .\mvnw.cmd clean package -DskipTests
cd ..\payments-service; .\mvnw.cmd clean package -DskipTests
cd ..                 # Vuelve a la raíz
```

Esto genera los JAR en `target/` dentro de cada servicio.

---
## 3. Configurar Credenciales

Puedes **editar** los `application.properties` o **pasar los valores como argumentos** al arrancar.

Ejemplo para `users-service`:
```powershell
java -jar target/users-service-*.jar \
  --spring.datasource.url="jdbc:sqlserver://localhost\\SQLEXPRESS;databaseName=users_db;encrypt=true;trustServerCertificate=true;" \
  --spring.datasource.username=users_service_user \
  --spring.datasource.password=TuClaveSQL \
  --jwt.secret=TuJwtEnBase64
```
Repite para los otros servicios cambiando URL, usuario, contraseña, puerto y `jwt.shared.secret`.

> **Importante**: El mismo secreto JWT (Base64) debe usarse en los tres microservicios.

---
## 4. Arrancar Rápido con Script

Copia el siguiente contenido en `start-services.cmd` en la raíz del proyecto y haz doble-clic:

```batch
@echo off
REM Lanza los microservicios en ventanas independientes
start "users"    cmd /k "cd users-service && .\mvnw.cmd spring-boot:run"
start "circuits" cmd /k "cd circuits-service && .\mvnw.cmd spring-boot:run"
start "payments" cmd /k "cd payments-service && .\mvnw.cmd spring-boot:run"
```

Puertos por defecto:
* Users → http://localhost:8081
* Circuits → http://localhost:8082
* Payments → http://localhost:8083

Comprueba el estado con `http://localhost:<puerto>/actuator/health` (si Actuator está habilitado).

Para **detener** los servicios, cierra cada ventana o pulsa `Ctrl + C` en su consola.

---
## 5. Arranque Manual (alternativa)

En tres terminales diferentes:
```powershell
# USERS
cd users-service
java -jar target\users-service-*.jar [propiedades]

# CIRCUITS
cd ..\circuits-service
java -jar target\circuits-service-*.jar [propiedades]

# PAYMENTS
cd ..\payments-service
java -jar target\payments-service-*.jar [propiedades]
```

---
## 6. Front-end Angular

```powershell
cd frontend
npm install       # solo la primera vez
npm start         # abre el navegador en http://localhost:4200
```

Para un build de producción:
```powershell
npm run build -- --configuration production
# Sirve la carpeta dist, por ejemplo:
npx http-server dist/circuits-frontend -p 8080
```

---
## 7. (Opcional) Docker Compose

Si prefieres contenedores:
1. Instala Docker Desktop.  
2. Crea un `docker-compose.yml` con servicios para **SQL Server**, **MySQL**, los **tres microservicios** (Dockerfile con `spring-boot:3.2-jdk17`), y un contenedor **Nginx** para el `dist` del front-end.

> Pide soporte si quieres que preparemos el archivo `docker-compose.yml` completo.

---
¡Con esto puedes lanzar y detener toda la aplicación cuando quieras!
