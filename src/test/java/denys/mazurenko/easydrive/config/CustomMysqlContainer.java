package denys.mazurenko.easydrive.config;

import org.testcontainers.containers.MySQLContainer;

public class CustomMysqlContainer extends MySQLContainer<CustomMysqlContainer> {
    private static final String DB_IMAGE = "mysql:8.0.33";

    private static CustomMysqlContainer container;

    private CustomMysqlContainer() {
        super(DB_IMAGE);
    }

    public static CustomMysqlContainer getInstance() {
        if (container == null) {
            container = new CustomMysqlContainer();
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("TEST_DB_URL", container.getJdbcUrl());
        System.setProperty("TEST_DB_USERNAME", container.getUsername());
        System.setProperty("TEST_DB_PASSWORD", container.getPassword());
    }

    @Override
    protected void doStart() {
        super.doStart();
    }
}
