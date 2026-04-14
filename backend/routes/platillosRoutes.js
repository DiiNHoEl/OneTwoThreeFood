const express = require('express');
const router = express.Router();
const platillosController = require('../controllers/platillosController');

router.post('/', platillosController.crearPlatillo);
router.get('/', platillosController.obtenerPlatillos);
router.get('/:id', platillosController.obtenerPlatilloPorId);
router.put('/:id', platillosController.actualizarPlatillo);
router.delete('/:id', platillosController.eliminarPlatillo);

module.exports = router;