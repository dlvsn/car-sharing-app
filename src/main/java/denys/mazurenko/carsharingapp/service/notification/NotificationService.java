package denys.mazurenko.carsharingapp.service.notification;

import denys.mazurenko.carsharingapp.model.Car;
import denys.mazurenko.carsharingapp.model.Payment;
import denys.mazurenko.carsharingapp.model.Rental;
import denys.mazurenko.carsharingapp.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final CarRentalServiceBot carRentalServiceBot;

    public void sendMessage(String message) {
        carRentalServiceBot.sendMessage(message);
    }

    public void sendNotificationRentCreated(Rental rental, User user, Car car) {
        carRentalServiceBot.sendMessage(
                TelegramBotMessages.rentalNotification(rental, user, car)
        );
    }

    public void sendNotificationRentCompleted(Rental rental, User user, Car car) {
        carRentalServiceBot.sendMessage(
                TelegramBotMessages.rentalCompleted(rental, user, car)
        );
    }

    public void sendNotificationPaymentCreated(Rental rental, User user, Payment payment) {
        carRentalServiceBot.sendMessage(
                TelegramBotMessages.paymentCreated(rental, user, payment)
        );
    }

    public void sendNotificationPaymentSuccess(Payment payment) {
        carRentalServiceBot.sendMessage(
                TelegramBotMessages.paymentPaid(payment)
        );
    }

    public void sendNotificationPaymentCancel(Payment payment) {
        carRentalServiceBot.sendMessage(
                TelegramBotMessages.paymentFailed(payment)
        );
    }

    public void sendNotificationActiveRentals(Rental rental, long minutesRemain) {
        carRentalServiceBot.sendMessage(
                TelegramBotMessages.activeRentals(rental, minutesRemain)
        );
    }

    public void sendNotificationOverdueRentals(Rental rental, long overdueMinutes) {
        carRentalServiceBot.sendMessage(
                TelegramBotMessages.overdueRentals(rental, overdueMinutes)
        );
    }
}
