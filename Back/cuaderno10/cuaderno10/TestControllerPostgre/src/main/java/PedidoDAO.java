import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PedidoDAO {
    private final IMotorSQL motor;
    public PedidoDAO(IMotorSQL motor) {
        this.motor = motor;
    }

    public boolean crearPedido(int userId, int articleId) {
        String sql = "INSERT INTO pedido(id_user, id_producto, fecha_pedido) VALUES (?, ?, now())";
        try (Connection con = motor.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, articleId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Error crearPedido: " + e.getMessage());
            return false;
        }
    }
}
