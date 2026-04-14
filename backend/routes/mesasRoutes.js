const express = require('express');
const router = express.Router();
const mesasController = require('../controllers/mesasController');

router.post('/', mesasController.crearMesa);
router.get('/', mesasController.obtenerMesas);
router.get('/:id', mesasController.obtenerMesaPorId);
router.put('/:id', mesasController.actualizarMesa);
router.delete('/:id', mesasController.eliminarMesa);

module.exports = router;