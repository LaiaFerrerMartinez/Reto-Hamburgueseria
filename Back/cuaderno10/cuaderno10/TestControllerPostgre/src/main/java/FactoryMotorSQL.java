public class FactoryMotorSQL {
    public static final String ORACLE = "ORACLE";
    public static final String MYSQL = "MYSQL";
    public static final String POSTGRE = "POSTGRE";
    public static final String AURORA = "AURORA";

    public static IMotorSQL getInstance(String bd) {
        IMotorSQL myMotor = null;

        switch (bd.toUpperCase()) {
            case ORACLE:
                // myMotor = new MotorOracle(); // Implementar si lo necesitas
                break;
            case POSTGRE:
                myMotor = new MotorPostgreSQL();
                break;
            case MYSQL:
                myMotor = new MotorMySQL();
                break;
            case AURORA:
                // No implementado
                break;
        }

        return myMotor;
    }
}
