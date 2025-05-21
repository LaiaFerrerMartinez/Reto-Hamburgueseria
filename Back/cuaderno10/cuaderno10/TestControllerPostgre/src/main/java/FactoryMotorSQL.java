public class FactoryMotorSQL {
    public static final String POSTGRE = "POSTGRE";
    // (ORACLE, MYSQL, AURORA si las implementas)

    public static IMotorSQL getInstance(String bd) {
        switch (bd.toUpperCase()) {
            case POSTGRE: return new MotorPostgreSQL();
            default: throw new IllegalArgumentException("Base de datos desconocida: " + bd);
        }
    }
}
