module.exports = (sequelize, DataTypes) => {
  const Mesa = sequelize.define('Mesa', {
    numero: {
      type: DataTypes.INTEGER,
      allowNull: false,
      unique: true
    },
    capacidad: {
      type: DataTypes.INTEGER,
      allowNull: false
    },
    estado: {
      type: DataTypes.ENUM('libre', 'ocupada', 'cerrada'),
      allowNull: false,
      defaultValue: 'libre'
    }
  });

  return Mesa;
};