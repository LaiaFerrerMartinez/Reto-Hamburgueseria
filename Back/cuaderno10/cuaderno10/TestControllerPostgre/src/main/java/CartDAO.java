// src/main/java/CartDAO.java
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartDAO {
    private IMotorSQL motor;
    public CartDAO(IMotorSQL motor) {
        this.motor = motor;
        this.motor.conectar();
    }

    public List<CartItem> getCartItems(int userId) throws SQLException {
        String sql = "SELECT p.id_producto, p.nombre_producto AS nombre, COUNT(*) AS cantidad,"
                + " p.precio_valor AS precioValor, p.img_producto AS imageUrl"
                + " FROM pedido d JOIN producto p ON d.id_producto=p.id_producto"
                + " WHERE d.id_user=? GROUP BY p.id_producto, p.nombre_producto, p.precio_valor, p.img_producto";
        List<CartItem> items = new ArrayList<>();
        try (Connection con = motor.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(new CartItem(
                            rs.getInt("id_producto"),
                            rs.getString("nombre"),
                            rs.getInt("cantidad"),
                            rs.getBigDecimal("precioValor"),
                            rs.getString("imageUrl")
                    ));
                }
            }
        }
        return items;
    }

    public boolean addCartItem(int userId, int productId) throws SQLException {
        String sql = "INSERT INTO pedido(id_user,id_producto,fecha_pedido) VALUES(?,?,now())";
        try (Connection con = motor.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            return ps.executeUpdate()==1;
        }
    }

    public boolean removeCartItem(int userId, int productId) throws SQLException {
        String sql = "DELETE FROM pedido WHERE ctid IN ("
                + " SELECT ctid FROM pedido WHERE id_user=? AND id_producto=? ORDER BY fecha_pedido DESC LIMIT 1"
                + ")";
        try (Connection con = motor.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            return ps.executeUpdate()==1;
        }
    }
}
