// CorreoDAO.java
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CorreoDAO {
    private final IMotorSQL motor;

    public CorreoDAO(IMotorSQL motor) {
        this.motor = motor;
    }

    // Inserta correo, devuelve false si ya existe
    public boolean insertarCorreo(String email) throws SQLException {
        String sql = "INSERT INTO correo(nombre_correo) VALUES(?)";
        try (Connection con = motor.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            // 23505 = unique_violation en PostgreSQL
            if ("23505".equals(e.getSQLState())) {
                return false; // duplicado
            }
            throw e;
        }
    }
}
