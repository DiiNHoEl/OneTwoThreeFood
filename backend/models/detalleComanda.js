module.exports = (sequelize, DataTypes) => {
  const DetalleComanda = sequelize.define('DetalleComanda', {
    cantidad: {
      type: DataTypes.INTEGER,
      allowNull: false
    },
    precioUnitario: {
      type: DataTypes.FLOAT,
      allowNull: false
    },
    subtotal: {
      type: DataTypes.FLOAT,
      allowNull: false
    }
  });

  return DetalleComanda;
};