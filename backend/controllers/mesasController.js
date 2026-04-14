const db = require('../models');
const Mesa = db.Mesa;

exports.crearMesa = async (req, res) => {
  try {
    const { numero, capacidad, estado } = req.body;

    if (!numero || !capacidad) {
      return res.status(400).json({
        success: false,
        message: 'Número y capacidad son requeridos'
      });
    }

    const mesaExistente = await Mesa.findOne({ where: { numero } });

    if (mesaExistente) {
      return res.status(409).json({
        success: false,
        message: 'Ya existe una mesa con ese número'
      });
    }

    const nuevaMesa = await Mesa.create({
      numero,
      capacidad,
      estado: estado || 'libre'
    });

    return res.status(201).json({
      success: true,
      message: 'Mesa creada correctamente',
      mesa: nuevaMesa
    });

  } catch (error) {
    console.error('Error al crear mesa:', error);
    return res.status(500).json({
      success: false,
      message: 'Error interno del servidor'
    });
  }
};

exports.obtenerMesas = async (req, res) => {
  try {
    const mesas = await Mesa.findAll();

    return res.status(200).json({
      success: true,
      mesas
    });

  } catch (error) {
    console.error('Error al obtener mesas:', error);
    return res.status(500).json({
      success: false,
      message: 'Error interno del servidor'
    });
  }
};

exports.obtenerMesaPorId = async (req, res) => {
  try {
    const { id } = req.params;

    const mesa = await Mesa.findByPk(id);

    if (!mesa) {
      return res.status(404).json({
        success: false,
        message: 'Mesa no encontrada'
      });
    }

    return res.status(200).json({
      success: true,
      mesa
    });

  } catch (error) {
    console.error('Error al obtener mesa:', error);
    return res.status(500).json({
      success: false,
      message: 'Error interno del servidor'
    });
  }
};

exports.actualizarMesa = async (req, res) => {
  try {
    const { id } = req.params;
    const { numero, capacidad, estado } = req.body;

    const mesa = await Mesa.findByPk(id);

    if (!mesa) {
      return res.status(404).json({
        success: false,
        message: 'Mesa no encontrada'
      });
    }

    await mesa.update({
      numero: numero ?? mesa.numero,
      capacidad: capacidad ?? mesa.capacidad,
      estado: estado ?? mesa.estado
    });

    return res.status(200).json({
      success: true,
      message: 'Mesa actualizada correctamente',
      mesa
    });

  } catch (error) {
    console.error('Error al actualizar mesa:', error);
    return res.status(500).json({
      success: false,
      message: 'Error interno del servidor'
    });
  }
};

exports.eliminarMesa = async (req, res) => {
  try {
    const { id } = req.params;

    const mesa = await Mesa.findByPk(id);

    if (!mesa) {
      return res.status(404).json({
        success: false,
        message: 'Mesa no encontrada'
      });
    }

    await mesa.destroy();

    return res.status(200).json({
      success: true,
      message: 'Mesa eliminada correctamente'
    });

  } catch (error) {
    console.error('Error al eliminar mesa:', error);
    return res.status(500).json({
      success: false,
      message: 'Error interno del servidor'
    });
  }
};