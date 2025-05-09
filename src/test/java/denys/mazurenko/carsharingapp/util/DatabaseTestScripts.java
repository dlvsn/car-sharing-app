package denys.mazurenko.carsharingapp.util;

public class DatabaseTestScripts {
    public static final String INSERT_USERS_SQL =
            "classpath:database/test/user/insert-user.sql";
    public static final String INSERT_CARS_SQL =
            "classpath:database/test/car/insert-car.sql";
    public static final String INSERT_RENTALS_SQL =
            "classpath:database/test/rental/insert-rental.sql";
    public static final String INSERT_PAYMENTS_SQL =
            "classpath:database/test/payment/insert-payment.sql";

    public static final String DELETE_PAYMENTS_SQL =
            "classpath:database/test/payment/delete-payment.sql";
    public static final String DELETE_RENTALS_SQL =
            "classpath:database/test/rental/delete-rental.sql";
    public static final String DELETE_USERS_SQL =
            "classpath:database/test/user/delete-user.sql";
    public static final String DELETE_ROLES_USERS_SQL =
            "classpath:database/test/user/delete-role.sql";
    public static final String DELETE_CARS_SQL =
            "classpath:database/test/car/delete-car.sql";

    private DatabaseTestScripts() {

    }
}
