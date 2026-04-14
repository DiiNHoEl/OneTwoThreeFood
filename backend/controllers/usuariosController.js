const db = require('../models');
const Usuario = db.Usuario;
const { Op } = require('sequelize');

exports.crearUsuario = async (req, res) => {
  try {
    const { nombre, usuario, contrasena, rol } = req.body;

    if (!nombre || !usuario || !contrasena || !rol) {
      return res.status(400).json({
        success: false,
        message: 'Nombre, usuario, contraseña y rol son requeridos'
      });
    }

    const usuarioExistente = await Usuario.findOne({ where: { usuario } });

    if (usuarioExistente) {
      return res.status(409).json({
        success: false,
        message: 'El usuario ya existe'
      });
    }

    const nuevoUsuario = await Usuario.create({
      nombre,
      usuario,
      contrasena,
      rol
    });

    return res.status(201).json({
      success: true,
      message: 'Usuario creado exitosamente',
      user: nuevoUsuario
    });

  } catch (error) {
    console.error('Error al crear usuario:', error);
    return res.status(500).json({
      success: false,
      message: 'Error interno del servidor'
    });
  }
};

exports.obtenerUsuarios = async (req, res) => {
  try {
    const usuarios = await Usuario.findAll({
      attributes: ['id', 'nombre', 'usuario', 'rol']
    });

    return res.status(200).json({
      success: true,
      usuarios
    });

  } catch (error) {
    console.error('Error al obtener usuarios:', error);
    return res.status(500).json({
      success: false,
      message: 'Error interno del servidor'
    });
  }
};

exports.obtenerUsuarioPorId = async (req, res) => {
  try {
    const { id } = req.params;

    const usuario = await Usuario.findByPk(id, {
      attributes: ['id', 'nombre', 'usuario', 'rol']
    });

    if (!usuario) {
      return res.status(404).json({
        success: false,
        message: 'Usuario no encontrado'
      });
    }

    return res.status(200).json({
      success: true,
      usuario
    });

  } catch (error) {
    console.error('Error al obtener usuario:', error);
    return res.status(500).json({
      success: false,
      message: 'Error interno del servidor'
    });
  }
};

exports.obtenerUsuarioPorNombre = async (req, res) => {
  try {
    const { nombre } = req.params;

    if (!nombre) {
      return res.status(400).json({
        success: false,
        message: 'El nombre es requerido'
      });
    }

    const usuarios = await Usuario.findAll({
      where: {
        nombre: {
          [Op.like]: `%${nombre}%`
        }
      },
      attributes: ['id', 'nombre', 'usuario', 'rol']
    });

    if (!usuarios || usuarios.length === 0) {
      return res.status(404).json({
        success: false,
        message: 'No se encontraron usuarios con ese nombre'
      });
    }

    return res.status(200).json({
      success: true,
      message: 'Usuarios encontrados',
      usuarios
    });

  } catch (error) {
    console.error('Error al buscar usuario por nombre:', error);
    return res.status(500).json({
      success: false,
      message: 'Error interno del servidor'
    });
  }
};

exports.obtenerUsuarioPorUsuario = async (req, res) => {
  try {
    const { usuario } = req.params;

    const user = await Usuario.findOne({
      where: { usuario },
      attributes: ['id', 'nombre', 'usuario', 'rol']
    });

    if (!user) {
      return res.status(404).json({
        success: false,
        message: 'Usuario no encontrado'
      });
    }

    return res.status(200).json({
      success: true,
      usuario: user
    });

  } catch (error) {
    console.error('Error al buscar usuario por usuario:', error);
    return res.status(500).json({
      success: false,
      message: 'Error interno del servidor'
    });
  }
};

exports.actualizarUsuario = async (req, res) => {
  try {
    const { id } = req.params;
    const { nombre, usuario, contrasena, rol } = req.body;

    const usuarioEncontrado = await Usuario.findByPk(id);

    if (!usuarioEncontrado) {
      return res.status(404).json({
        success: false,
        message: 'Usuario no encontrado'
      });
    }

    await usuarioEncontrado.update({
      nombre: nombre ?? usuarioEncontrado.nombre,
      usuario: usuario ?? usuarioEncontrado.usuario,
      contrasena: contrasena ?? usuarioEncontrado.contrasena,
      rol: rol ?? usuarioEncontrado.rol
    });

    return res.status(200).json({
      success: true,
      message: 'Usuario actualizado correctamente',
      usuario: {
        id: usuarioEncontrado.id,
        nombre: usuarioEncontrado.nombre,
        usuario: usuarioEncontrado.usuario,
        rol: usuarioEncontrado.rol
      }
    });

  } catch (error) {
    console.error('Error al actualizar usuario:', error);
    return res.status(500).json({
      success: false,
      message: 'Error interno del servidor'
    });
  }
};

exports.eliminarUsuario = async (req, res) => {
  try {
    const { id } = req.params;

    const usuarioEncontrado = await Usuario.findByPk(id);

    if (!usuarioEncontrado) {
      return res.status(404).json({
        success: false,
        message: 'Usuario no encontrado'
      });
    }

    await usuarioEncontrado.destroy();

    return res.status(200).json({
      success: true,
      message: 'Usuario eliminado correctamente'
    });

  } catch (error) {
    console.error('Error al eliminar usuario:', error);
    return res.status(500).json({
      success: false,
      message: 'Error interno del servidor'
    });
  }
};