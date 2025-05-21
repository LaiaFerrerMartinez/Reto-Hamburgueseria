
import java.sql.*;

public class ReservaDAO {
    private IMotorSQL motor;

    public ReservaDAO(IMotorSQL motor) {
        this.motor = motor;
        this.motor.conectar();
    }

    public boolean crearReserva(ReservaRequest req) throws SQLException {
        Connection con = motor.getConnection();

        // 1) Obtener id_sitio y capacidad_sitio (nombre de columna corregido)
        String sqlSitio = "SELECT id_sitio, capacidad_sitio " +
                "FROM sitio WHERE nombre_sitio = ?";
        int idSitio, capacidad;
        try (PreparedStatement psSitio = con.prepareStatement(sqlSitio)) {
            psSitio.setString(1, req.getSitioName());
            try (ResultSet rs = psSitio.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Sitio no encontrado: " + req.getSitioName());
                }
                idSitio   = rs.getInt("id_sitio");
                capacidad = rs.getInt("capacidad_sitio");  // ← aquí
            }
        }

        // 2) Calcular suma actual de personas para esa fecha y hora
        String sqlSum = "SELECT COALESCE(SUM(personas_reserva),0) AS total " +
                "FROM reserva " +
                "WHERE id_sitio = ? AND fecha_reserva = ? AND hora_reserva = ?";
        int sumaActual;
        try (PreparedStatement psSum = con.prepareStatement(sqlSum)) {
            psSum.setInt(1, idSitio);
            psSum.setString(2, req.getDate());
            psSum.setString(3, req.getTime());
            try (ResultSet rs = psSum.executeQuery()) {
                rs.next();
                sumaActual = rs.getInt("total");
            }
        }

        // 3) Validar capacidad ANTES de insertar
        int solicitadas = req.getNumPeople();
        if (sumaActual + solicitadas > capacidad) {
            throw new SQLException(
                    "Capacidad excedida: ya hay " + sumaActual +
                            " reservados, solicitas " + solicitadas +
                            ", capacidad máxima " + capacidad
            );
        }

        // 4) Insertar reserva
        String sqlIns = "INSERT INTO reserva(" +
                "nombre_reserva, fecha_reserva, hora_reserva, personas_reserva, id_sitio, id_user" +
                ") VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement psIns = con.prepareStatement(sqlIns)) {
            psIns.setString(1, req.getName());
            psIns.setString(2, req.getDate());
            psIns.setString(3, req.getTime());
            psIns.setInt(4, solicitadas);
            psIns.setInt(5, idSitio);
            psIns.setInt(6, req.getUserId());
            return psIns.executeUpdate() == 1;
        }
    }
}
