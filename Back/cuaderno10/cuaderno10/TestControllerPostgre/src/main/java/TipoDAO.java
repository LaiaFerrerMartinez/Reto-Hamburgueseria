// src/main/java/TipoDAO.java
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TipoDAO {
    private final IMotorSQL motor;

    public TipoDAO(IMotorSQL motor) {
        this.motor = motor;
        this.motor.conectar();
    }

    public static class Tipo {
        public int id_tipo;
        public String nombre_tipo;
        public Tipo(int id, String nombre) {
            this.id_tipo     = id;
            this.nombre_tipo = nombre;
        }
    }

    /** Lista todos los tipos (id + nombre) */
    public List<Tipo> listarTodos() throws SQLException {
        String sql = "SELECT id_tipo, nombre_tipo FROM tipo ORDER BY nombre_tipo";
        List<Tipo> lista = new ArrayList<>();
        try (Connection con = motor.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Tipo(
                        rs.getInt("id_tipo"),
                        rs.getString("nombre_tipo")
                ));
            }
        }
        return lista;
    }
}
