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

    /**
     * Autentica por email + contraseña en texto claro.
     * Lee el hash de la BD y lo verifica con BCrypt.
     */
    public Usuario autenticar(String email, String plainPassword) {
        String sql = "SELECT id_user, username, email_user, type_user, password_user "
                + "FROM usuario WHERE email_user = ?";
        try (Connection con = motor.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_user");
                    // Si coincide el hash, devolvemos el usuario
                    if (PasswordUtils.checkPassword(plainPassword, storedHash)) {
                        return new Usuario(
                                rs.getInt("id_user"),
                                rs.getString("username"),
                                rs.getString("email_user"),
                                rs.getString("type_user")
                        );
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Error en autenticación: " + e.getMessage());
        }
        // Si no encuentra o no coincide, devolvemos null
        return null;
    }

    /**
     * Registro de usuario: hashea la contraseña antes de insertar.
     * Devuelve el objeto Usuario con el nuevo ID.
     */
    public Usuario registrar(String email, String username, String plainPassword)
            throws SQLException, Exception {
        String insertSql = "INSERT INTO usuario(email_user, password_user, username, type_user) "
                + "VALUES (?, ?, ?, '') RETURNING id_user";

        try (Connection con = motor.getConnection();
             PreparedStatement insertStmt = con.prepareStatement(insertSql)) {

            // 1) Calculamos el hash BCrypt
            String hashed = PasswordUtils.hashPassword(plainPassword);

            insertStmt.setString(1, email);
            insertStmt.setString(2, hashed);
            insertStmt.setString(3, username);

            // 2) Obtenemos el nuevo ID
            int newId;
            try (ResultSet rs = insertStmt.executeQuery()) {
                if (!rs.next()) {
                    throw new Exception("No se obtuvo id del nuevo usuario");
                }
                newId = rs.getInt("id_user");
            }

            // 3) Leemos type_user (queda vacío por defecto)
            String selectSql = "SELECT type_user FROM usuario WHERE id_user = ?";
            try (PreparedStatement selectStmt = con.prepareStatement(selectSql)) {
                selectStmt.setInt(1, newId);
                try (ResultSet rs2 = selectStmt.executeQuery()) {
                    String type = rs2.next() ? rs2.getString("type_user") : "";
                    return new Usuario(newId, username, email, type);
                }
            }
        }
    }

    /**
     * Obtiene un usuario por su ID (sin contraseña).
     */
    public Usuario getById(int id) throws SQLException {
        String sql = "SELECT id_user, username, email_user, type_user "
                + "FROM usuario WHERE id_user = ?";
        try (Connection con = motor.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                            rs.getInt("id_user"),
                            rs.getString("username"),
                            rs.getString("email_user"),
                            rs.getString("type_user")
                    );
                }
                return null;
            }
        }
    }

    /**
     * Verifica la contraseña para un usuario dado su ID.
     */
    public boolean checkPassword(int userId, String plainPassword) throws SQLException {
        String sql = "SELECT password_user FROM usuario WHERE id_user = ?";
        try (Connection con = motor.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_user");
                    return PasswordUtils.checkPassword(plainPassword, storedHash);
                }
            }
        }
        return false;
    }

}
