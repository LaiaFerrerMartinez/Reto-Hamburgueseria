import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PedidoAntiguoDAO {
    private IMotorSQL motor;
    public PedidoAntiguoDAO(IMotorSQL motor) {
        this.motor = motor;
    }

    public int checkout(int userId) throws SQLException {
        String insert = ""
                + "INSERT INTO pedido_antiguo(id_user, fecha_pedido_antiguo, id_producto) "
                + "SELECT id_user, NOW(), id_producto FROM pedido WHERE id_user = ?";
        String delete = "DELETE FROM pedido WHERE id_user = ?";

        // Abrir y cerrar conexión en cada bloque
        try (Connection con = motor.getConnection();
             PreparedStatement psIns = con.prepareStatement(insert);
             PreparedStatement psDel = con.prepareStatement(delete)) {

            // 1) Insertar en antiguo
            psIns.setInt(1, userId);
            int moved = psIns.executeUpdate();

            // 2) Borrar del pedido activo
            psDel.setInt(1, userId);
            psDel.executeUpdate();

            return moved;
        }
    }

    public String listProductsForEmail(int userId) throws SQLException {
        String sql = ""
                + "SELECT p.nombre_producto AS nombre, COUNT(*) AS qty "
                + "FROM pedido "
                + "JOIN producto p ON pedido.id_producto = p.id_producto "
                + "WHERE pedido.id_user = ? "
                + "GROUP BY p.nombre_producto";

        StringBuilder sb = new StringBuilder();
        try (Connection con = motor.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    sb.append("- ")
                            .append(rs.getString("nombre"))
                            .append(" x")
                            .append(rs.getInt("qty"))
                            .append("\n");
                }
            }
        }
        return sb.toString();
    }

}
