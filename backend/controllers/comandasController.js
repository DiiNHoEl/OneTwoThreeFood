const db = require('../models');
const Comanda = db.Comanda;
const DetalleComanda = db.DetalleComanda;
const Mesa = db.Mesa;
const Usuario = db.Usuario;
const Platillo = db.Platillo;

exports.crearComanda = async (req, res) => {
  try {
    const { mesaId, meseroId, detalles } = req.body;

    if (!mesaId || !meseroId || !detalles || !Array.isArray(detalles) || detalles.length === 0) {
      return res.status(400).json({
        success: false,
        message: 'mesaId, meseroId y detalles son requeridos'
      });
    }

    const mesa = await Mesa.findByPk(mesaId);
    if (!mesa) {
      return res.status(404).json({
        success: false,
        message: 'Mesa no encontrada'
      });
    }

    const mesero = await Usuario.findByPk(meseroId);
    if (!mesero || mesero.rol !== 'mesero') {
      return res.status(400).json({
        success: false,
        message: 'El usuario indicado no es un mesero válido'
      });
    }

    const comandaActiva = await Comanda.findOne({
      where: {
        mesaId,
        estado: ['pendiente', 'preparando', 'listo']
      }
    });

    if (comandaActiva) {
      return res.status(409).json({
        success: false,
        message: 'La mesa ya tiene una comanda activa'
      });
    }

    let total = 0;

    const nuevaComanda = await Comanda.create({
      mesaId,
      meseroId,
      estado: 'pendiente',
      total: 0
    });

    for (const item of detalles) {
      const platillo = await Platillo.findByPk(item.platilloId);

      if (!platillo) {
        return res.status(404).json({
          success: false,
          message: `Platillo con id ${item.platilloId} no encontrado`
        });
      }

      const cantidad = parseInt(item.cantidad);
      const precioUnitario = platillo.precio;
      const subtotal = cantidad * precioUnitario;

      total += subtotal;

      await DetalleComanda.create({
        comandaId: nuevaComanda.id,
        platilloId: platillo.id,
        cantidad,
        precioUnitario,
        subtotal
      });
    }

    await nuevaComanda.update({ total });
    await mesa.update({ estado: 'ocupada' });

    return res.status(201).json({
      success: true,
      message: 'Comanda creada correctamente',
      comanda: nuevaComanda
    });

  } catch (error) {
    console.error('Error al crear comanda:', error);
    return res.status(500).json({
      success: false,
      message: 'Error interno del servidor'
    });
  }
};

exports.obtenerComandas = async (req, res) => {
  try {
    const comandas = await Comanda.findAll({
      include: [
        { model: Mesa, as: 'mesa' },
        { model: Usuario, as: 'mesero', attributes: ['id', 'nombre', 'usuario', 'rol'] },
        {
          model: DetalleComanda,
          as: 'detalles',
          include: [
            { model: Platillo, as: 'platillo' }
          ]
        }
      ]
    });

    return res.status(200).json({
      success: true,
      comandas
    });

  } catch (error) {
    console.error('Error al obtener comandas:', error);
    return res.status(500).json({
      success: false,
      message: 'Error interno del servidor'
    });
  }
};

exports.obtenerComandaPorId = async (req, res) => {
  try {
    const { id } = req.params;

    const comanda = await Comanda.findByPk(id, {
      include: [
        { model: Mesa, as: 'mesa' },
        { model: Usuario, as: 'mesero', attributes: ['id', 'nombre', 'usuario', 'rol'] },
        {
          model: DetalleComanda,
          as: 'detalles',
          include: [
            { model: Platillo, as: 'platillo' }
          ]
        }
      ]
    });

    if (!comanda) {
      return res.status(404).json({
        success: false,
        message: 'Comanda no encontrada'
      });
    }

    return res.status(200).json({
      success: true,
      comanda
    });

  } catch (error) {
    console.error('Error al obtener comanda:', error);
    return res.status(500).json({
      success: false,
      message: 'Error interno del servidor'
    });
  }
};

exports.actualizarEstadoComanda = async (req, res) => {
  try {
    const { id } = req.params;
    const { estado } = req.body;

    const estadosValidos = ['pendiente', 'preparando', 'listo', 'pagado'];

    if (!estado || !estadosValidos.includes(estado)) {
      return res.status(400).json({
        success: false,
        message: 'Estado no válido'
      });
    }

    const comanda = await Comanda.findByPk(id);

    if (!comanda) {
      return res.status(404).json({
        success: false,
        message: 'Comanda no encontrada'
      });
    }

    await comanda.update({ estado });

    return res.status(200).json({
      success: true,
      message: 'Estado de comanda actualizado',
      comanda
    });

  } catch (error) {
    console.error('Error al actualizar estado de comanda:', error);
    return res.status(500).json({
      success: false,
      message: 'Error interno del servidor'
    });
  }
};

exports.obtenerComandasPendientesCocina = async (req, res) => {
  try {
    const comandas = await Comanda.findAll({
      where: {
        estado: ['pendiente', 'preparando']
      },
      include: [
        { model: Mesa, as: 'mesa' },
        { model: Usuario, as: 'mesero', attributes: ['id', 'nombre', 'usuario', 'rol'] },
        {
          model: DetalleComanda,
          as: 'detalles',
          include: [
            { model: Platillo, as: 'platillo' }
          ]
        }
      ]
    });

    return res.status(200).json({
      success: true,
      comandas
    });

  } catch (error) {
    console.error('Error al obtener comandas para cocina:', error);
    return res.status(500).json({
      success: false,
      message: 'Error interno del servidor'
    });
  }
};

exports.obtenerComandasListasParaCobro = async (req, res) => {
  try {
    const comandas = await Comanda.findAll({
      where: {
        estado: 'listo'
      },
      include: [
        { model: Mesa, as: 'mesa' },
        { model: Usuario, as: 'mesero', attributes: ['id', 'nombre', 'usuario', 'rol'] },
        {
          model: DetalleComanda,
          as: 'detalles',
          include: [
            { model: Platillo, as: 'platillo' }
          ]
        }
      ]
    });

    return res.status(200).json({
      success: true,
      comandas
    });

  } catch (error) {
    console.error('Error al obtener comandas listas para cobro:', error);
    return res.status(500).json({
      success: false,
      message: 'Error interno del servidor'
    });
  }
};

exports.cobrarComanda = async (req, res) => {
  try {
    const { id } = req.params;

    const comanda = await Comanda.findByPk(id);

    if (!comanda) {
      return res.status(404).json({
        success: false,
        message: 'Comanda no encontrada'
      });
    }

    if (comanda.estado !== 'listo') {
      return res.status(400).json({
        success: false,
        message: 'Solo se pueden cobrar comandas finalizadas/listas'
      });
    }

    await comanda.update({ estado: 'pagado' });

    const mesa = await Mesa.findByPk(comanda.mesaId);
    if (mesa) {
      await mesa.update({ estado: 'libre' });
    }

    return res.status(200).json({
      success: true,
      message: 'Comanda cobrada correctamente',
      comanda
    });

  } catch (error) {
    console.error('Error al cobrar comanda:', error);
    return res.status(500).json({
      success: false,
      message: 'Error interno del servidor'
    });
  }
};