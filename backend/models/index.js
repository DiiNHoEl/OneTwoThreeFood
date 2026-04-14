const { Sequelize } = require('sequelize');

const sequelize = new Sequelize({
  dialect: 'sqlite',
  storage: './database.sqlite',
  logging: false
});

const db = {};

db.Sequelize = Sequelize;
db.sequelize = sequelize;

db.Usuario = require('./usuario')(sequelize, Sequelize);
db.Mesa = require('./mesa')(sequelize, Sequelize);
db.Platillo = require('./platillo')(sequelize, Sequelize);
db.Comanda = require('./comanda')(sequelize, Sequelize);
db.DetalleComanda = require('./detalleComanda')(sequelize, Sequelize);

/*
RELACIONES
*/

// Usuario (mesero) -> muchas comandas
db.Usuario.hasMany(db.Comanda, {
  foreignKey: 'meseroId',
  as: 'comandasMesero'
});
db.Comanda.belongsTo(db.Usuario, {
  foreignKey: 'meseroId',
  as: 'mesero'
});

// Mesa -> muchas comandas
db.Mesa.hasMany(db.Comanda, {
  foreignKey: 'mesaId',
  as: 'comandas'
});
db.Comanda.belongsTo(db.Mesa, {
  foreignKey: 'mesaId',
  as: 'mesa'
});

// Comanda -> muchos detalles
db.Comanda.hasMany(db.DetalleComanda, {
  foreignKey: 'comandaId',
  as: 'detalles'
});
db.DetalleComanda.belongsTo(db.Comanda, {
  foreignKey: 'comandaId',
  as: 'comanda'
});

// Platillo -> muchos detalles
db.Platillo.hasMany(db.DetalleComanda, {
  foreignKey: 'platilloId',
  as: 'detallesPlatillo'
});
db.DetalleComanda.belongsTo(db.Platillo, {
  foreignKey: 'platilloId',
  as: 'platillo'
});

module.exports = db;