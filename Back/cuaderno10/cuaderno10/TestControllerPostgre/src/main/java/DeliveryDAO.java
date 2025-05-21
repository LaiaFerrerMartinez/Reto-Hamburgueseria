import java.sql.*;

public class DeliveryDAO {
    private IMotorSQL motor;

    public DeliveryDAO(IMotorSQL motor) {
        this.motor = motor;
        this.motor.conectar();
    }

    public boolean crearDelivery(DeliveryRequest req) throws SQLException {
        Connection con = motor.getConnection();
        String sql = "INSERT INTO delivery(" +
                "  nombre_delivery, address_delivery, tlf_delivery, id_user" +
                ") VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, req.getName());
            ps.setString(2, req.getAddress());
            ps.setInt(3, Integer.parseInt(req.getPhoneNumber()));
            ps.setInt(4, req.getUserId());
            return ps.executeUpdate() == 1;
        }
    }
}
