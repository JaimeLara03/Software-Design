# Guía de Instalación y Ejecución de Circuits

Esta guía explica paso a paso cómo clonar, instalar y ejecutar correctamente tanto el backend como el frontend de la aplicación Circuits.

## Clonar el Repositorio

```bash
# Clonar el repositorio
git clone <url_del_repositorio> circuits
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

Navega a la carpeta raíz del proyecto si no estás ya en ella:

```bash
cd circuits
```

Compila el proyecto para instalar todas las dependencias:

```bash
mvn clean install -DskipTests
```

> **Nota**: Usamos `-DskipTests` para acelerar la instalación inicial. Una vez configurado todo, puedes ejecutar las pruebas con `mvn test`.

### Configurar el Frontend

Abre una nueva terminal y navega a la carpeta del frontend:

```bash
cd circuits/frontend
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

> **Importante**: El perfil de prueba desactiva la seguridad para facilitar el desarrollo y las pruebas. NO utilices este perfil en producción.

El backend estará disponible en: http://localhost:8080

### Iniciar el Frontend

En una nueva terminal, desde la carpeta frontend:

```bash
cd circuits/frontend
ng serve
```

El frontend estará disponible en: http://localhost:4200

## Limpieza del Proyecto

### Reducir el Tamaño del Repositorio

Para minimizar el tamaño del repositorio antes de subirlo a GitHub o compartirlo, elimina los siguientes archivos que ocupan mucho espacio y son generados automáticamente durante la compilación:

```bash
# Ejecutar desde la raíz del proyecto

# 1. Limpiar archivos compilados del backend (elimina la carpeta target, ~150MB)
cd circuits
mvn clean

# 2. Limpiar dependencias y archivos generados del frontend (puede liberar hasta ~700MB)
cd frontend
rm -rf node_modules/    # ~400-500MB de dependencias de npm
rm -rf .angular/        # ~200-300MB de caché de compilación
rm -rf dist/            # ~1-5MB de archivos compilados

# 3. Eliminar caché local de npm
npm cache clean --force

# 4. Eliminar archivos de log si existen
cd ..
find . -name "*.log" -type f -delete

# 5. Verificar tamaño actual del proyecto
du -sh .
```

### Detalle de Archivos Eliminados y Espacio Ahorrado

| Directorio/Archivo | Tamaño Aproximado | Descripción |
|-------------------|-------------------|------------|
| `target/` | 150-200MB | Archivos compilados del backend Java/Maven |
| `frontend/node_modules/` | 400-500MB | Dependencias de Node.js para el frontend |
| `frontend/.angular/` | 200-300MB | Caché de compilación de Angular CLI |
| `frontend/dist/` | 1-5MB | Frontend compilado para producción |
| `*.log` | Variable | Archivos de registro que crecen con el uso |

### ¿Por qué Es Importante Eliminar Estos Archivos?

1. **Reduce drásticamente el tamaño del repositorio**: El proyecto puede pasar de ocupar ~1GB a solo ~10-20MB
2. **Acelera operaciones de git**: Clonado, push y pull son mucho más rápidos con un repositorio pequeño
3. **Evita conflictos**: Los archivos generados automáticamente pueden causar conflictos innecesarios
4. **Mejora la legibilidad**: Los cambios en el código fuente son más fáciles de seguir
5. **Ahorra espacio en GitHub**: Los repositorios tienen límites de tamaño

### Script para Limpieza Rápida

Puedes crear un script de limpieza en la raíz del proyecto:

```bash
# Crear un script de limpieza
cat > clean-repo.sh << 'EOF'
#!/bin/bash
echo "Limpiando repositorio..."

# Guardar tamaño inicial
INITIAL_SIZE=$(du -sh . | cut -f1)

# Limpiar backend
mvn clean

# Limpiar frontend
cd frontend
rm -rf node_modules/ .angular/ dist/
npm cache clean --force
cd ..

# Eliminar logs
find . -name "*.log" -type f -delete

# Mostrar tamaño final
FINAL_SIZE=$(du -sh . | cut -f1)
echo "Limpieza completada!"
echo "Tamaño antes: $INITIAL_SIZE"
echo "Tamaño después: $FINAL_SIZE"
EOF

# Hacer el script ejecutable
chmod +x clean-repo.sh
```

Luego puedes ejecutar `./clean-repo.sh` cada vez que quieras limpiar el proyecto.

### Regenerar Después de Clonar

Cuando alguien clone el repositorio limpio, simplemente deberá seguir las instrucciones de instalación de este README para regenerar estos archivos automáticamente.
