#!/bin/bash
echo "Limpiando repositorio..."

# Guardar tamaño inicial
INITIAL_SIZE=$(du -sh . | cut -f1)

# Limpiar backend
echo "Limpiando archivos compilados del backend..."
mvn clean

# Limpiar frontend
echo "Limpiando archivos del frontend..."
cd frontend
if [ -d "node_modules" ]; then
  echo "Eliminando node_modules (~400-500MB)..."
  rm -rf node_modules/
fi

if [ -d ".angular" ]; then
  echo "Eliminando caché de Angular (~200-300MB)..."
  rm -rf .angular/
fi

if [ -d "dist" ]; then
  echo "Eliminando carpeta dist..."
  rm -rf dist/
fi

echo "Limpiando caché de npm..."
npm cache clean --force
cd ..

# Eliminar logs
echo "Eliminando archivos de log..."
find . -name "*.log" -type f -delete

# Mostrar tamaño final
FINAL_SIZE=$(du -sh . | cut -f1)
echo "¡Limpieza completada!"
echo "Tamaño antes: $INITIAL_SIZE"
echo "Tamaño después: $FINAL_SIZE"
echo ""
echo "El repositorio está listo para ser subido a GitHub."
