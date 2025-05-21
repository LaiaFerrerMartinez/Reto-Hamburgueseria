import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class MotorPostgreSQL implements IMotorSQL {
    private static final String URL  = "jdbc:postgresql://reto-hamburgueseria.cp88sg4ac10g.us-east-1.rds.amazonaws.com/postgres";
    private static final String USER = "postgres";
    private static final String PASS = "12345678";

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError("Driver PostgreSQL no encontrado: " + e.getMessage());
        }
    }

    @Override
    public void conectar() {
        Connection c = null;
        try {
            c = getConnection();
            System.out.println("✅ Conexión a PostgreSQL OK");
        } catch (Exception e) {
            System.err.println("❌ Error en conectar(): " + e.getMessage());
        } finally {
            if (c != null) try { c.close(); } catch (SQLException ignored) {}
        }
    }

    @Override
    public Connection getConnection() {
        try {
            Properties props = new Properties();
            props.setProperty("user", USER);
            props.setProperty("password", PASS);
            props.setProperty("ssl", "false");
            return DriverManager.getConnection(URL, props);
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo obtener conexión a PostgreSQL", e);
        }
    }
}
