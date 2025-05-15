import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductoDAO {
    private final IMotorSQL motor;

    public ProductoDAO(IMotorSQL motor) {
        this.motor = motor;
        this.motor.conectar();
    }

    // Sobrecarga para compatibilidad (sin orden)
    public List<Producto> listarPorTipo(String tipo) throws SQLException {
        return listarPorTipo(tipo, null);
    }

    // Con orden opcional por precio_valor
    public List<Producto> listarPorTipo(String tipo, String sort) throws SQLException {
        String orderClause = "";
        if ("asc".equalsIgnoreCase(sort) || "desc".equalsIgnoreCase(sort)) {
            String dir = "desc".equalsIgnoreCase(sort) ? "DESC" : "ASC";
            orderClause = " ORDER BY p.precio_valor " + dir;
        }

        String sql =
                "SELECT p.id_producto, p.nombre_producto, p.id_tipo, p.img_producto, " +
                        "       p.precio_valor, p.precio_producto " +
                        "FROM producto p " +
                        "WHERE p.id_tipo = ( " +
                        "  SELECT t.id_tipo FROM tipo t " +
                        "  WHERE LOWER(t.nombre_tipo) = LOWER(?) " +
                        "  LIMIT 1" +
                        ")" +
                        orderClause;

        List<Producto> lista = new ArrayList<>();
        Set<Integer> vistos = new HashSet<>();

        try (Connection con = motor.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, tipo);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id       = rs.getInt("id_producto");
                    if (!vistos.add(id)) continue;

                    String nombre           = rs.getString("nombre_producto");
                    BigDecimal precioValor  = rs.getBigDecimal("precio_valor");
                    String precioFormateado = rs.getString("precio_producto");
                    String img              = rs.getString("img_producto");

                    lista.add(new Producto(
                            id,
                            nombre,
                            precioFormateado,
                            precioValor,
                            img
                    ));
                }
            }
        }
        return lista;
    }
}
