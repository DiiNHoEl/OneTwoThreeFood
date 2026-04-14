# 🍽️ Sistema de Restaurante - App Android + API Backend

## 📌 Descripción del Proyecto

Este proyecto consiste en el desarrollo de un sistema de gestión para restaurante, compuesto por:

- 📱 Aplicación móvil en Android (Kotlin)
- 🌐 API Backend en Node.js con Express
- 🗄️ Base de datos SQLite manejada con Sequelize

El sistema permite la gestión completa del flujo operativo de un restaurante:

1. Administración de usuarios, mesas y platillos
2. Registro de comandas por meseros
3. Preparación de pedidos en cocina
4. Cobro de comandas por cajero

---

## 🎯 Objetivo

Automatizar el flujo de trabajo dentro de un restaurante, permitiendo:

- Mejor organización
- Reducción de errores manuales
- Control de pedidos en tiempo real

---

## 🧱 Arquitectura del Sistema


Android App (Frontend)

↓

API REST (Node.js + Express)

↓

Base de Datos (SQLite + Sequelize)


---

## 👤 Roles del Sistema

### 🔹 Admin
- Gestiona usuarios
- Gestiona mesas
- Gestiona platillos

### 🔹 Mesero
- Consulta mesas
- Consulta platillos
- Registra comandas

### 🔹 Cocina
- Consulta comandas pendientes
- Cambia estado a:
    - preparando
    - listo

### 🔹 Cajero
- Consulta comandas listas
- Cobra comandas
- Libera mesas

---

## ⚙️ Tecnologías Utilizadas

### Frontend (Android)
- Kotlin
- Android Studio
- Material Design

### Backend
- Node.js
- Express
- Sequelize ORM

### Base de Datos
- SQLite

---

## 🚀 Instalación del Backend

### 1. Clonar el proyecto

git clone <repo-url>
cd bE

### 2. Instalar dependencias
npm install

### 3. Ejecutar el servidor
node server.js

---

## ⚠️ Nota importante sobre SQLite

Si aparece un error como:

SequelizeUniqueConstraintError

puedes solucionarlo:

Eliminando el archivo:
database.sqlite

o cambiando en server.js:

sequelize.sync()

---

## 📱 Instalación del Frontend (Android)
Abrir el proyecto en Android Studio
Verificar conexión con backend (URL en Retrofit)
Ejecutar en emulador o dispositivo físico

---

## 🔗 Endpoints Principales

👤 Usuarios
POST /usuarios
GET /usuarios/:id
GET /usuarios/nombre/:nombre
GET /usuarios/usuario/:usuario
PUT /usuarios/:id
DELETE /usuarios/:id

🍽️ Mesas
POST /mesas
GET /mesas
GET /mesas/:id
PUT /mesas/:id
DELETE /mesas/:id

🍔 Platillos
POST /platillos
GET /platillos
GET /platillos/:id
PUT /platillos/:id
DELETE /platillos/:id

🧾 Comandas
POST /comandas
GET /comandas/pendientes/cocina
GET /comandas/listas/caja
PUT /comandas/:id/estado
PUT /comandas/:id/cobrar

---

## 🔄 Flujo del Sistema
👤 Admin crea:
Usuarios
Mesas
Platillos
🧑‍🍳 Mesero:
Registra comanda
🍳 Cocina:
Cambia estado → preparando → listo
💰 Cajero:
Cobra comanda
Mesa queda libre

---

## 🧠 Funcionalidades Clave
Login por roles
Persistencia opcional (Recordar usuario)
CRUD completo (usuarios, mesas, platillos)
Manejo de estados de comanda
Control de flujo realista de restaurante

---

## 📌 Mejoras Futuras
Uso de Spinner en lugar de EditText (mejor UX)
Soporte para múltiples platillos por comanda
Notificaciones en tiempo real
Dashboard administrativo
Migración a base de datos remota

---

## 👨‍💻 Autor

Proyecto desarrollado como parte de la materia:

Desarrollo de Aplicaciones Móviles Multiplataforma (DAMM)

---

---

# Ejecución

## ▶️ Instrucciones de Ejecución

### 🔙 Backend (API Node.js)

El backend se ejecuta desde la terminal utilizando Node.js.

### 1. Abrir terminal en la carpeta del backend

cd bE

### 2. Ejecutar el servidor

node server.js

Si todo funciona correctamente, verás un mensaje como:

Servidor corriendo en http://localhost:3000

---

### 🌐 Exponer el servidor a internet (Port Forwarding en VS Code)

Para poder conectar la app Android con el backend, es necesario exponer el servidor.

Pasos:
Abrir Visual Studio Code
Ir a la pestaña "Ports" (Puertos)
Agregar el puerto 3000
Activar el Port Forwarding
VS Code generará una URL pública, por ejemplo:
https://abc123-3000.usw3.devtunnels.ms/

---

### 🔁 Configuración en Android (MUY IMPORTANTE)

La URL generada debe reemplazarse en los archivos .kt donde se usa Retrofit.

Buscar esta línea:

private const val BASE_URL = "https://TU-URL-AQUI/"

Y reemplazarla por la URL generada, por ejemplo:

private const val BASE_URL = "https://abc123-3000.usw3.devtunnels.ms/"

---

### 📍 Archivos donde debes hacer el cambio

MainActivity.kt
AdminPersonal.kt
AdminMesas.kt
AdminPlatillos.kt
Mesero.kt
Cocina.kt
Cajero.kt

⚠️ Es importante que todas las activities usen la misma URL.

---

### 📱 Frontend (Android App)

🛠️ Requisitos
Android Studio Panda 2 | 2025.3.2
SDK Android configurado
Emulador o dispositivo físico

---

### ▶️ Ejecutar la aplicación

Abrir el proyecto en Android Studio
Esperar a que cargue Gradle
Conectar un dispositivo o iniciar un emulador
Ejecutar:
Run 'app'

o presionar el botón ▶️

---

### 📡 Conexión con el backend

Antes de ejecutar la app, asegúrate de:

El backend esté corriendo (node server.js)
El port forwarding esté activo
La URL esté correctamente configurada en los archivos .kt

---

### ✅ Flujo de prueba recomendado

Iniciar backend
Activar port forwarding
Configurar URL en Android
Ejecutar app
Probar:
Login
Admin (crear datos)
Mesero (crear comanda)
Cocina (cambiar estado)
Cajero (cobrar)

---

### ⚠️ Problemas comunes

❌ Error de conexión desde Android
Verificar que el backend esté activo
Revisar que la URL sea correcta
Confirmar que el puerto 3000 esté expuesto