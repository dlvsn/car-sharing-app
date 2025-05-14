package denys.mazurenko.easydrive.service.notification.payment;

import denys.mazurenko.easydrive.model.Car;
import denys.mazurenko.easydrive.model.Payment;
import denys.mazurenko.easydrive.model.Rental;
import denys.mazurenko.easydrive.model.User;
import denys.mazurenko.easydrive.service.notification.bot.RentalServiceBot;
import denys.mazurenko.easydrive.service.notification.util.MessageBuilder;
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
