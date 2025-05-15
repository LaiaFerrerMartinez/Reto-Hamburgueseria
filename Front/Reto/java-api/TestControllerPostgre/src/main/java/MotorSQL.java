import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class MotorSQL {
    private Connection conn;
    private static final String URL="jdbc:postgresql://database-1.c3smyaeku6nl.us-east-1.rds.amazonaws.com/postgres";
    //jdbc:postgresql://postgrespring.c9aqgoyi6x61.us-east-1.rds.amazonaws.com/postgres"
    private static final String USER="postgres";
    private static final String PASSWORD="12345678";
    public void conectar(){
        try{
            Class.forName("org.postgresql.Driver");
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        Properties properties = new Properties();
        //properties.setProperty("url", "postgrespring.c9aqgoyi6x61.us-east-1.rds.amazonaws.com");
        properties.setProperty("user",USER);
        properties.setProperty("password", PASSWORD);
        properties.setProperty("ssl", "false");
        try {
            conn = DriverManager.getConnection(URL, properties);
            if (conn != null) {
                System.out.println("Connected to the database!");
            }else{
                System.out.println("Error al conectar");
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
}
