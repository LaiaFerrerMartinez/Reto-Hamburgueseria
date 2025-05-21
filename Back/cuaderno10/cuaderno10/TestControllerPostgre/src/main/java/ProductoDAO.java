import java.math.BigDecimal;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

public class ProductoDAO {
    private final IMotorSQL motor;

    public ProductoDAO(IMotorSQL motor) {
        this.motor = motor;
        this.motor.conectar();
    }

    /** Listar por id_tipo, opcional orden por precio */
    public List<Producto> listarPorTipoId(int idTipo, String sort) throws SQLException {
        String sql =
                "SELECT p.id_producto, p.nombre_producto, p.descripcion_producto, " +
                        "       p.precio_valor, p.img_producto, " +
                        "       COALESCE(r.total_votes,0) AS total_votes, " +
                        "       COALESCE(r.avg_rating,0.0) AS avg_rating " +
                        "FROM producto p " +
                        "LEFT JOIN producto_rating_summary r ON p.id_producto = r.id_producto " +
                        "WHERE p.id_tipo = ?";
        if ("asc".equalsIgnoreCase(sort) || "desc".equalsIgnoreCase(sort)) {
            sql += " ORDER BY p.precio_valor " + ("desc".equalsIgnoreCase(sort) ? "DESC" : "ASC");
        }
        return ejecutarListado(sql, ps -> ps.setInt(1, idTipo));
    }

    /** Fallback: listar por nombre de tipo */
    public List<Producto> listarPorTipo(String nombreTipo, String sort) throws SQLException {
        String sql =
                "SELECT p.id_producto, p.nombre_producto, p.descripcion_producto, " +
                        "       p.precio_valor, p.img_producto, " +
                        "       COALESCE(r.total_votes,0) AS total_votes, " +
                        "       COALESCE(r.avg_rating,0.0) AS avg_rating " +
                        "FROM producto p " +
                        "LEFT JOIN producto_rating_summary r ON p.id_producto = r.id_producto " +
                        "WHERE p.id_tipo = (" +
                        "  SELECT id_tipo FROM tipo WHERE LOWER(nombre_tipo)=LOWER(?) LIMIT 1" +
                        ")";
        if ("asc".equalsIgnoreCase(sort) || "desc".equalsIgnoreCase(sort)) {
            sql += " ORDER BY p.precio_valor " + ("desc".equalsIgnoreCase(sort) ? "DESC" : "ASC");
        }
        return ejecutarListado(sql, ps -> ps.setString(1, nombreTipo));
    }

    public void create(Producto p) throws SQLException {
        String sql =
                "INSERT INTO producto " +
                        "  (id_tipo, nombre_producto, descripcion_producto, precio_valor, img_producto) " +
                        "VALUES (?, ?, ?, ?, ?)";
        try (
                Connection con = motor.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setInt(1, p.getTypeId());
            ps.setString(2, p.getNombre());
            ps.setString(3, p.getDescripcion());
            ps.setBigDecimal(4, p.getPrecioValor());
            ps.setString(5, p.getImg());
            ps.executeUpdate();
        }
    }

    /** Modifica un producto existente */
    public void update(Producto p) throws SQLException {
        String sql =
                "UPDATE producto SET " +
                        "  id_tipo = ?, " +
                        "  nombre_producto = ?, " +
                        "  descripcion_producto = ?, " +
                        "  precio_valor = ?, " +
                        "  img_producto = ? " +
                        "WHERE id_producto = ?";
        try (
                Connection con = motor.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setInt(1, p.getTypeId());
            ps.setString(2, p.getNombre());
            ps.setString(3, p.getDescripcion());
            ps.setBigDecimal(4, p.getPrecioValor());
            ps.setString(5, p.getImg());
            ps.setInt(6, p.getId());
            ps.executeUpdate();
        }
    }

    /** Elimina por id_producto */
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM producto WHERE id_producto = ?";
        try (
                Connection con = motor.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /** Guarda una valoración */
    public void saveRating(int idProducto, Integer idUsuario, int rating) throws SQLException {
        String sql = "INSERT INTO producto_rating (id_producto, id_usuario, rating) VALUES (?, ?, ?)";
        try (Connection con = motor.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            if (idUsuario != null) ps.setInt(2, idUsuario);
            else ps.setNull(2, Types.INTEGER);
            ps.setInt(3, rating);
            ps.executeUpdate();
        }
    }

    /** Obtiene el resumen de valoraciones */
    public RatingSummary getRatingSummary(int idProducto) {
        String sql = "SELECT total_votes, avg_rating FROM producto_rating_summary WHERE id_producto = ?";
        try (Connection con = motor.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new RatingSummary(rs.getInt("total_votes"),
                            rs.getDouble("avg_rating"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new RatingSummary(0, 0.0);
    }

    // --- método interno para listar y mapear resultados ---
    private List<Producto> ejecutarListado(String sql, SQLConsumer<PreparedStatement> preparador)
            throws SQLException {
        List<Producto> lista = new ArrayList<>();
        Set<Integer> vistos  = new HashSet<>();

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("es","ES"));
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');
        DecimalFormat df = new DecimalFormat("#,##0.00", symbols);

        try (Connection con = motor.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            preparador.accept(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id_producto");
                    if (!vistos.add(id)) continue;

                    String nombre   = rs.getString("nombre_producto");
                    BigDecimal valor= rs.getBigDecimal("precio_valor");
                    String precioF  = df.format(valor);
                    String img      = rs.getString("img_producto");
                    String desc     = rs.getString("descripcion_producto");
                    int votes       = rs.getInt("total_votes");
                    double avg      = rs.getDouble("avg_rating");

                    lista.add(new Producto(
                            id, nombre, precioF, valor, img, desc, votes, avg
                    ));
                }
            }
        }
        return lista;
    }
    public List<Producto> listarMasVotadosYCaros() throws SQLException {
        String sql =
                "SELECT p.id_producto, p.nombre_producto, p.descripcion_producto, " +
                        "       p.precio_valor, p.img_producto, " +
                        "       COALESCE(r.total_votes,0) AS total_votes, " +
                        "       COALESCE(r.avg_rating,0.0) AS avg_rating " +
                        "FROM producto p " +
                        "LEFT JOIN producto_rating_summary r ON p.id_producto = r.id_producto " +
                        "ORDER BY r.total_votes DESC, p.precio_valor DESC";

        return ejecutarListado(sql, ps -> {});
    }



    @FunctionalInterface
    private interface SQLConsumer<T> {
        void accept(T t) throws SQLException;
    }
}
