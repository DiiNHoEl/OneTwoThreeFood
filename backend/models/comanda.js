module.exports = (sequelize, DataTypes) => {
  const Comanda = sequelize.define('Comanda', {
    estado: {
      type: DataTypes.ENUM('pendiente', 'preparando', 'listo', 'pagado'),
      allowNull: false,
      defaultValue: 'pendiente'
    },
    total: {
      type: DataTypes.FLOAT,
      allowNull: false,
      defaultValue: 0
    },
    fecha: {
      type: DataTypes.DATE,
      allowNull: false,
      defaultValue: DataTypes.NOW
    }
  });

  return Comanda;
};