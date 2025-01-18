package denys.mazurenko.carsharingapp.service.bot;

import denys.mazurenko.carsharingapp.model.Car;
import denys.mazurenko.carsharingapp.model.Payment;
import denys.mazurenko.carsharingapp.model.Rental;
import denys.mazurenko.carsharingapp.model.User;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss");

    private final CarRentalServiceBot carRentalServiceBot;

    public void sendMessage(String message) {
        carRentalServiceBot.sendMessage(message);
    }

    public void sendNotificationRentCreated(Rental rental, User user, Car car) {
        carRentalServiceBot.sendMessage(String.format(
                TelegramBotMessages.getRENTAL_NOTIFICATION(),
                rental.getId(),
                user.getEmail(),
                rental.getRentalDate().format(DATE_TIME_FORMATTER),
                rental.getReturnDate().format(DATE_TIME_FORMATTER),
                car.getBrand(),
                car.getModel()
        ));
    }

    public void sendNotificationRentCompleted(Rental rental, User user, Car car) {
        carRentalServiceBot.sendMessage(String.format(TelegramBotMessages.getRENTAL_COMPLETED(),
                rental.getId(),
                user.getEmail(),
                rental.getRentalDate().format(DATE_TIME_FORMATTER),
                rental.getReturnDate().format(DATE_TIME_FORMATTER),
                rental.getActualReturnDate().format(DATE_TIME_FORMATTER),
                car.getBrand(),
                car.getModel()
        ));
    }

    public void sendNotificationPaymentCreated(Rental rental, User user, Payment payment) {
        carRentalServiceBot.sendMessage(
                String.format(
                        TelegramBotMessages.getPAYMENT_CREATED(),
                        rental.getId(),
                        user.getEmail(),
                        rental.getCar().getBrand(),
                        rental.getCar().getModel(),
                        rental.getRentalDate(),
                        rental.getReturnDate(),
                        rental.getActualReturnDate(),
                        payment.getAmount(),
                        payment.getStatus().name()
                )
        );
    }

    public void sendNotificationPaymentSuccess(Payment payment) {
        carRentalServiceBot.sendMessage(
                String.format(
                        TelegramBotMessages.getPAYMENT_PAID(),
                        payment.getId(),
                        payment.getAmount(),
                        payment.getStatus().name()
                )
        );
    }

    public void sendNotificationPaymentCancel(Payment payment) {
        carRentalServiceBot.sendMessage(
                String.format(
                        TelegramBotMessages.getPAYMENT_FAILED(),
                        payment.getId(),
                        payment.getAmount(),
                        payment.getStatus().name()
                )
        );
    }
}
