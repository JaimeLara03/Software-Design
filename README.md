# Guía de Instalación y Ejecución de Circuits

Esta guía explica paso a paso cómo clonar, instalar y ejecutar correctamente tanto el backend como el frontend de la aplicación Circuits.

## Clonar el Repositorio

```bash
# Clonar el repositorio
git clone https://github.com/JaimeLara03/Software-Design.git circuits
cd circuits
```

## Prerequisitos

### Instalar Node.js y npm

```bash
sudo apt update
sudo apt install nodejs npm -y
```

Verifica las versiones instaladas:

```bash
node -v && npm -v
```

Se recomienda Node.js 16.x o superior y npm 8.x o superior.

### Instalar Angular CLI

```bash
sudo npm install -g @angular/cli
```

Verifica la instalación:

```bash
ng version
```

### Instalar Java JDK 17

```bash
sudo apt install openjdk-17-jdk -y
```

Verifica la instalación:

```bash
java -version
```

### Instalar Maven

```bash
sudo apt install maven -y
```

Verifica la instalación:

```bash
mvn -v
```

## Configuración del Proyecto

### Configurar el Backend

Navega a la carpeta raíz del proyecto si no estás ya en ella.
Compila el proyecto para instalar todas las dependencias:

```bash
mvn clean install -DskipTests
```

> **Nota**: Usamos `-DskipTests` para acelerar la instalación inicial. Una vez configurado todo, puedes ejecutar las pruebas con `mvn test`.

### Configurar el Frontend

Abre una nueva terminal y navega a la carpeta del frontend:

```bash
cd frontend
```

Instala las dependencias del frontend:

```bash
npm install
```

## Ejecutar la Aplicación

### Iniciar el Backend en Modo de Prueba

Desde la carpeta raíz del proyecto:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=test
```

Puede ser que tengas problemas a la hora de inicializar el backend. Si tienes problemas, ejecuta el siguiente comando en lugar de el de arriba:

```bash
mvn spring-boot:run
```

> **Importante**: El perfil de prueba desactiva la seguridad para facilitar el desarrollo y las pruebas. NO utilices este perfil en producción.

El backend estará disponible en: http://localhost:8080

### Iniciar el Frontend

En una nueva terminal, desde la carpeta frontend:

```bash
cd frontend
ng serve
```

El frontend estará disponible en: http://localhost:4200

Luego puedes ejecutar `./clean-repo.sh` cada vez que quieras limpiar el proyecto.

### Regenerar Después de Clonar

Cuando alguien clone el repositorio limpio, simplemente deberá seguir las instrucciones de instalación de este README para regenerar estos archivos automáticamente.
