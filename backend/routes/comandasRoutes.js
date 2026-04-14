const express = require('express');
const router = express.Router();
const comandasController = require('../controllers/comandasController');

router.post('/', comandasController.crearComanda);
router.get('/', comandasController.obtenerComandas);
router.get('/pendientes/cocina', comandasController.obtenerComandasPendientesCocina);
router.get('/listas/caja', comandasController.obtenerComandasListasParaCobro);
router.get('/:id', comandasController.obtenerComandaPorId);
router.put('/:id/estado', comandasController.actualizarEstadoComanda);
router.put('/:id/cobrar', comandasController.cobrarComanda);

module.exports = router;