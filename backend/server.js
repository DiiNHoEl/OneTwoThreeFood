const express = require('express');
const cors = require('cors');

const db = require('./models');

const authRoutes = require('./routes/authRoutes');
const usuariosRoutes = require('./routes/usuariosRoutes');
const mesasRoutes = require('./routes/mesasRoutes');
const platillosRoutes = require('./routes/platillosRoutes');
const comandasRoutes = require('./routes/comandasRoutes');

const app = express();
const PORT = 3000;

app.use(cors({
  origin: '*',
  methods: ['GET', 'POST', 'PUT', 'DELETE'],
  allowedHeaders: ['Content-Type', 'Authorization']
}));

app.use(express.json());
app.use(express.urlencoded({ extended: true }));

app.get('/', (req, res) => {
  res.json({
    success: true,
    message: 'API del restaurante funcionando correctamente'
  });
});

app.use('/auth', authRoutes);
app.use('/usuarios', usuariosRoutes);
app.use('/mesas', mesasRoutes);
app.use('/platillos', platillosRoutes);
app.use('/comandas', comandasRoutes);

db.sequelize.sync()
  .then(() => {
    console.log('Base de datos sincronizada');
    app.listen(PORT, () => {
      console.log(`Servidor corriendo en http://localhost:${PORT}`);
    });
  })
  .catch((error) => {
    console.error('Error al sincronizar la base de datos:', error);
  });