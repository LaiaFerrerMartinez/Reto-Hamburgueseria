import java.sql.Connection;

public abstract class MotorSQLA implements IMotorSQL {
    protected Connection conn;

    @Override
    public Connection getConnection() {
        return conn;
    }
}
