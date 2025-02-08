package denys.mazurenko.carsharingapp.config;

import denys.mazurenko.carsharingapp.service.notification.payment.PaymentNotificationService;
import denys.mazurenko.carsharingapp.service.notification.rental.RentalNotificationService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {

    @Bean
    public PaymentNotificationService paymentNotificationService() {
        return Mockito.mock(PaymentNotificationService.class);
    }

    @Bean
    public RentalNotificationService rentalNotificationService() {
        return Mockito.mock(RentalNotificationService.class);
    }
}
