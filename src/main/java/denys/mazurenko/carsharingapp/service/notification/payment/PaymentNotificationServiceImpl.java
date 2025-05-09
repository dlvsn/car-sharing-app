package denys.mazurenko.carsharingapp.service.notification.payment;

import denys.mazurenko.carsharingapp.model.Car;
import denys.mazurenko.carsharingapp.model.Payment;
import denys.mazurenko.carsharingapp.model.Rental;
import denys.mazurenko.carsharingapp.model.User;
import denys.mazurenko.carsharingapp.service.notification.bot.RentalServiceBot;
import denys.mazurenko.carsharingapp.service.notification.util.MessageBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Async
@RequiredArgsConstructor
@Service
public class PaymentNotificationServiceImpl implements PaymentNotificationService {
    private final RentalServiceBot bot;

    @Override
    public void sendNotificationPaymentCreated(Rental rental, User user, Car car, Payment payment) {
        String message = MessageBuilder.builder()
                .addPaymentSessionCreatedHeader()
                .addId(rental.getId())
                .addUser(user.getEmail())
                .addCar(car.getBrand(), car.getModel())
                .addRentalDate(rental.getRentalDate())
                .addReturnDate(rental.getReturnDate())
                .addActualDate(rental.getActualReturnDate())
                .addTotalAmount(payment.getAmount())
                .addPaymentStatus(payment.getStatus())
                .build();
        bot.sendNotification(message);
    }

    @Override
    public void sendNotificationPaymentSuccess(Payment payment) {
        String message = MessageBuilder.builder()
                .addPaymentCompletedHeader()
                .addId(payment.getId())
                .addTotalAmount(payment.getAmount())
                .addPaymentStatus(payment.getStatus())
                .build();
        bot.sendNotification(message);
    }

    @Override
    public void sendNotificationPaymentCancel(Payment payment) {
        String message = MessageBuilder.builder()
                .addPaymentFailedHeader()
                .addId(payment.getId())
                .addTotalAmount(payment.getAmount())
                .addPaymentStatus(payment.getStatus())
                .build();
        bot.sendNotification(message);
    }
}
