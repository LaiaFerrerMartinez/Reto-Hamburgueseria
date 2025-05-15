import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class HamburguesaDAO {
    private IMotorSQL motor;

    public HamburguesaDAO(IMotorSQL motor) {
        this.motor = motor;
        this.motor.conectar();
    }

    public List<Hamburguesa> listarHamburguesas() {
        List<Hamburguesa> hamburguesas = new ArrayList<>();
        String sql = "SELECT id, nombre, precio FROM hamburguesas";

        try {
            Connection con = motor.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Hamburguesa h = new Hamburguesa(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getDouble("precio")
                );
                hamburguesas.add(h);
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.out.println("Error al listar hamburguesas: " + e.getMessage());
        }

        return hamburguesas;
    }
}
