# Blossom E-commerce — Frontend

Interfaz web de Blossom E-commerce, desarrollada con React, TypeScript y Vite. Se comunica con la API Spring Boot del backend.

## Requisitos

- Node.js compatible con Vite 8 (Node 20.19+ o 22.12+).
- npm, incluido con Node.js.
- Backend iniciado en `http://localhost:8080` para usar las funciones conectadas a la API.

## Instalación

Abre una terminal en la carpeta `frontend/frontend`, donde se encuentran `package.json` y `package-lock.json`:

```powershell
npm install
```

## Ejecutar en desarrollo

```powershell
npm run dev
```

Vite muestra la dirección local al iniciar; por defecto es `http://localhost:5173`. El backend permite solicitudes CORS desde ese origen.

La URL base de la API está definida en `src/api/axiosConfig.ts` como `http://localhost:8080/api`. Si el backend usa otra dirección, actualiza esa configuración.

## Construir y previsualizar

Comprueba los tipos y genera la versión de producción:

```powershell
npm run build
```

Los archivos generados se guardan en `dist/`. Para servir esa compilación localmente:

```powershell
npm run preview
```

## Pruebas y cobertura

Ejecuta las pruebas unitarias con Vitest:

```powershell
npm test
```

El comando genera cobertura con el proveedor V8 y verifica que líneas, funciones, ramas y sentencias alcancen al menos 80%. El informe HTML se encuentra en `coverage/index.html`.

## Calidad de código

```powershell
npm run lint
```

## Funcionalidades principales

- Catálogo de productos con búsqueda y filtros por categoría y rango de precio.
- Formularios para registro, inicio de sesión y creación de productos.
- Persistencia del JWT de sesión en `localStorage` y envío del token en las solicitudes API.
- Carrito de compras e historial de órdenes.
- Rutas de la interfaz: `/`, `/auth`, `/admin/products/new` y `/account`.

## Estructura de carpetas

```text
frontend/
├── package.json             # Dependencias y scripts npm
├── package-lock.json        # Versiones fijadas de dependencias
├── vite.config.ts           # Vite, Vitest y umbrales de cobertura
├── tsconfig*.json           # Configuración de TypeScript
├── public/                  # Recursos públicos servidos sin transformación
└── src/
    ├── api/                 # Cliente HTTP y configuración de Axios
    ├── assets/              # Imágenes y recursos importados por la aplicación
    ├── components/          # Componentes compartidos de interfaz
    ├── context/             # Estado compartido del carrito
    ├── hooks/               # Hooks para productos y carrusel
    ├── pages/               # Vistas asociadas a las rutas
    ├── test/                # Pruebas unitarias, organizadas por área
    │   ├── api/
    │   ├── components/
    │   ├── context/
    │   ├── hooks/
    │   └── pages/
    ├── types/               # Tipos de dominio de TypeScript
    ├── utils/               # Utilidades compartidas
    ├── App.tsx              # Enrutamiento y composición principal
    ├── main.tsx             # Punto de entrada de React
    └── setupTest.ts         # Configuración común de pruebas
```
