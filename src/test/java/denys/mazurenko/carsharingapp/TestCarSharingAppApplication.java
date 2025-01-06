package denys.mazurenko.carsharingapp;

import org.springframework.boot.SpringApplication;

public class TestCarSharingAppApplication {

    public static void main(String[] args) {
        SpringApplication.from(CarSharingAppApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
