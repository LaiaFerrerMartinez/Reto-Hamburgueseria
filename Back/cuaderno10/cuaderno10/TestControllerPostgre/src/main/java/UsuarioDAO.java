import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {
    private IMotorSQL motor;

    public UsuarioDAO(IMotorSQL motor) {
        this.motor = motor;
        this.motor.conectar();
    }

    public Usuario autenticar(String username, String password) {
        String sql = "SELECT id_user, username, email_user FROM usuario WHERE username = ? AND password_user = ?";
        Usuario usuario = null;

        try {
            Connection con = motor.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                usuario = new Usuario(
                        rs.getInt("id_user"),
                        rs.getString("username"),
                        rs.getString("email_user")
                );
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.out.println("Error en autenticación: " + e.getMessage());
        }

        return usuario;
    }

    public Usuario registrar(String email, String username, String password) throws SQLException, Exception {
        String sql = "INSERT INTO usuario(email_user, password_user, username, type_user) " +
                "VALUES (?, ?, ?, '') RETURNING id_user";
        try (Connection con = motor.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            stmt.setString(3, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id_user");
                    return new Usuario(id, username, email);
                } else {
                    throw new Exception("No se obtuvo id del nuevo usuario");
                }
            }
        }
    }
}