# Frontend de Gestión de Circuitos

Este proyecto es la interfaz de usuario para la aplicación de Gestión de Circuitos, desarrollada con Angular. Proporciona una interfaz moderna y responsive para interactuar con el backend de circuitos cuánticos.

## Características

- **Gestión de Circuitos**:
  - Crear circuitos cuánticos con número personalizado de qubits
  - Recuperar y visualizar detalles de circuitos existentes

- **Gestión de Cuentas de Usuario**:
  - Registro de nuevos usuarios
  - Verificación de cuentas
  - Inicio de sesión
  - Recuperación de contraseña

- **Sistema de Pagos**:
  - Integración con diversos métodos de pago
  - Historial de transacciones
  - Gestión de crédito para circuitos avanzados

## Requisitos Previos

- Node.js (v14.0.0 o superior)
- npm (v6.0.0 o superior)
- Angular CLI (v16.1.0 o superior)

## Instalación

1. Clonar el repositorio
2. Navegar al directorio del frontend:
   ```bash
   cd /home/jaime/Escritorio/circuits/frontend
   ```
3. Instalar dependencias:
   ```bash
   npm install
   ```
4. Iniciar el servidor de desarrollo:
   ```bash
   ng serve
   ```
5. Abrir el navegador en `http://localhost:4200`

## Integración con Backend

El frontend está configurado para comunicarse con el backend Spring Boot en `http://localhost:8080`. Asegúrate de que el backend esté en ejecución antes de utilizar las funcionalidades que requieren comunicación con el servidor.

## Estructura del Proyecto

- `src/app/components`: Componentes de la aplicación organizados por funcionalidad
  - `circuit`: Componentes relacionados con la gestión de circuitos
  - `user`: Componentes relacionados con la gestión de usuarios
  - `home`: Página principal
  - `navbar`: Barra de navegación

- `src/app/services`: Servicios para comunicación con el backend
  - `auth.service.ts`: Gestión de autenticación
  - `circuit.service.ts`: Operaciones con circuitos
  - `payment.service.ts`: Procesamiento de pagos

- `src/app/models`: Interfaces de datos
- `src/app/interceptors`: Interceptores HTTP para autenticación
- `src/app/guards`: Guards para protección de rutas

## Despliegue en Producción

Para generar los archivos de producción:

```bash
ng build --prod
```

Los archivos generados estarán en el directorio `dist/` y pueden ser desplegados en cualquier servidor web estático.

## Licencia

Este proyecto es privado y está destinado únicamente para uso interno.
