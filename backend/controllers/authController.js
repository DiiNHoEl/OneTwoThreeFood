const db = require('../models');
const Usuario = db.Usuario;

exports.login = async (req, res) => {
  try {
    const { usuario, contrasena } = req.body;

    if (!usuario || !contrasena) {
      return res.status(400).json({
        success: false,
        message: 'Usuario y contraseña son requeridos'
      });
    }

    const user = await Usuario.findOne({ where: { usuario } });

    if (!user) {
      return res.status(404).json({
        success: false,
        message: 'Usuario no encontrado'
      });
    }

    if (user.contrasena !== contrasena) {
      return res.status(401).json({
        success: false,
        message: 'Contraseña incorrecta'
      });
    }

    return res.status(200).json({
      success: true,
      message: 'Login exitoso',
      user: {
        id: user.id,
        nombre: user.nombre,
        usuario: user.usuario,
        rol: user.rol
      }
    });

  } catch (error) {
    console.error('Error en login:', error);
    return res.status(500).json({
      success: false,
      message: 'Error interno del servidor'
    });
  }
};