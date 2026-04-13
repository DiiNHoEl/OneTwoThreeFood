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

```bash
git clone <repo-url>
cd bE
2. Instalar dependencias
npm install
3. Ejecutar el servidor
node server.js
⚠️ Nota importante sobre SQLite

Si aparece un error como:

SequelizeUniqueConstraintError

puedes solucionarlo:

Eliminando el archivo:
database.sqlite

o cambiando en server.js:

sequelize.sync()
📱 Instalación del Frontend (Android)
Abrir el proyecto en Android Studio
Verificar conexión con backend (URL en Retrofit)
Ejecutar en emulador o dispositivo físico
🔗 Endpoints Principales
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
🔄 Flujo del Sistema
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
🧠 Funcionalidades Clave
Login por roles
Persistencia opcional (Recordar usuario)
CRUD completo (usuarios, mesas, platillos)
Manejo de estados de comanda
Control de flujo realista de restaurante
📌 Mejoras Futuras
Uso de Spinner en lugar de EditText (mejor UX)
Soporte para múltiples platillos por comanda
Notificaciones en tiempo real
Dashboard administrativo
Migración a base de datos remota
👨‍💻 Autor

Proyecto desarrollado como parte de la materia:

Desarrollo de Aplicaciones Móviles Multiplataforma (DAMM)
