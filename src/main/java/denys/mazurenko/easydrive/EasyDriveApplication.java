package denys.mazurenko.easydrive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class EasyDriveApplication {
    public static void main(String[] args) {
        SpringApplication.run(EasyDriveApplication.class, args);
    }
}
