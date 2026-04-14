const db = require('../models');
const Platillo = db.Platillo;

exports.crearPlatillo = async (req, res) => {
  try {
    const { nombre, descripcion, precio, categoria, disponible } = req.body;

    if (!nombre || precio == null || !categoria) {
      return res.status(400).json({
        success: false,
        message: 'Nombre, precio y categoría son requeridos'
      });
    }

    const nuevoPlatillo = await Platillo.create({
      nombre,
      descripcion,
      precio,
      categoria,
      disponible: disponible ?? true
    });

    return res.status(201).json({
      success: true,
      message: 'Platillo creado correctamente',
      platillo: nuevoPlatillo
    });

  } catch (error) {
    console.error('Error al crear platillo:', error);
    return res.status(500).json({
      success: false,
      message: 'Error interno del servidor'
    });
  }
};

exports.obtenerPlatillos = async (req, res) => {
  try {
    const platillos = await Platillo.findAll();

    return res.status(200).json({
      success: true,
      platillos
    });

  } catch (error) {
    console.error('Error al obtener platillos:', error);
    return res.status(500).json({
      success: false,
      message: 'Error interno del servidor'
    });
  }
};

exports.obtenerPlatilloPorId = async (req, res) => {
  try {
    const { id } = req.params;

    const platillo = await Platillo.findByPk(id);

    if (!platillo) {
      return res.status(404).json({
        success: false,
        message: 'Platillo no encontrado'
      });
    }

    return res.status(200).json({
      success: true,
      platillo
    });

  } catch (error) {
    console.error('Error al obtener platillo:', error);
    return res.status(500).json({
      success: false,
      message: 'Error interno del servidor'
    });
  }
};

exports.actualizarPlatillo = async (req, res) => {
  try {
    const { id } = req.params;
    const { nombre, descripcion, precio, categoria, disponible } = req.body;

    const platillo = await Platillo.findByPk(id);

    if (!platillo) {
      return res.status(404).json({
        success: false,
        message: 'Platillo no encontrado'
      });
    }

    await platillo.update({
      nombre: nombre ?? platillo.nombre,
      descripcion: descripcion ?? platillo.descripcion,
      precio: precio ?? platillo.precio,
      categoria: categoria ?? platillo.categoria,
      disponible: disponible ?? platillo.disponible
    });

    return res.status(200).json({
      success: true,
      message: 'Platillo actualizado correctamente',
      platillo
    });

  } catch (error) {
    console.error('Error al actualizar platillo:', error);
    return res.status(500).json({
      success: false,
      message: 'Error interno del servidor'
    });
  }
};

exports.eliminarPlatillo = async (req, res) => {
  try {
    const { id } = req.params;

    const platillo = await Platillo.findByPk(id);

    if (!platillo) {
      return res.status(404).json({
        success: false,
        message: 'Platillo no encontrado'
      });
    }

    await platillo.destroy();

    return res.status(200).json({
      success: true,
      message: 'Platillo eliminado correctamente'
    });

  } catch (error) {
    console.error('Error al eliminar platillo:', error);
    return res.status(500).json({
      success: false,
      message: 'Error interno del servidor'
    });
  }
};