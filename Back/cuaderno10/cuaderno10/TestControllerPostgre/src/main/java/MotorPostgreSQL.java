import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class MotorPostgreSQL extends MotorSQLA {
    @Override
    public void conectar() {
        try {
            Class.forName("org.postgresql.Driver");
            Properties props = new Properties();
            props.setProperty("user", "postgres"); // Cambia si tu usuario no es postgres
            props.setProperty("password", "12345678");
            props.setProperty("ssl", "false");

            conn = DriverManager.getConnection(
                    "jdbc:postgresql://reto-hamburgueseria.cp88sg4ac10g.us-east-1.rds.amazonaws.com/postgres", props);

            System.out.println("Conexión a la base de datos RDS exitosa.");
        } catch (Exception e) {
            System.out.println("Error de conexión a PostgreSQL: " + e.getMessage());
        }
    }
}

