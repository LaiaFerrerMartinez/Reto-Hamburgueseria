import java.sql.Connection;

public interface IMotorSQL {
    /** Solo para prueba rápida; no es obligatorio llamarlo en producción */
    void conectar();
    /** Devuelve siempre una conexión nueva */
    Connection getConnection();
}
