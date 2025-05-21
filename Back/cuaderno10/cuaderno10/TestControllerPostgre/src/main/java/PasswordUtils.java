import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {
    // Genera el hash (incluye salt)
    public static String hashPassword(String plainPassword) {
        // Coste 12 es un buen compromiso entre seguridad y rendimiento
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }

    // Verifica si la contraseña en claro coincide con el hash
    public static boolean checkPassword(String plainPassword, String hashed) {
        if (hashed == null || !hashed.startsWith("$2a$")) {
            throw new IllegalArgumentException("Hash inválido");
        }
        return BCrypt.checkpw(plainPassword, hashed);
    }
}
