const express = require('express');
const router = express.Router();
const usuariosController = require('../controllers/usuariosController');

router.post('/', usuariosController.crearUsuario);
router.get('/', usuariosController.obtenerUsuarios);

// IMPORTANTE: estas rutas específicas van antes de "/:id"
router.get('/nombre/:nombre', usuariosController.obtenerUsuarioPorNombre);
router.get('/usuario/:usuario', usuariosController.obtenerUsuarioPorUsuario);

router.get('/:id', usuariosController.obtenerUsuarioPorId);
router.put('/:id', usuariosController.actualizarUsuario);
router.delete('/:id', usuariosController.eliminarUsuario);

module.exports = router;