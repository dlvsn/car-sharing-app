package denys.mazurenko.carsharingapp.service.notification;

import denys.mazurenko.carsharingapp.model.Car;
import denys.mazurenko.carsharingapp.model.Payment;
import denys.mazurenko.carsharingapp.model.Rental;
import denys.mazurenko.carsharingapp.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Async
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final CarRentalServiceBot carRentalServiceBot;

    public void sendMessage(String message) {
        carRentalServiceBot.sendMessage(message);
    }

    public void sendNotificationRentalCompleted(Rental rental, User user, Car car) {
        String message = MessageBuilder.builder()
                .addRentalCompletedHeader()
                .addId(rental.getId())
                .addUser(user.getEmail())
                .addCar(car.getBrand(), car.getModel())
                .addRentalDate(rental.getRentalDate())
                .addReturnDate(rental.getReturnDate())
                .addActualDate(rental.getActualReturnDate())
                .addRentalStatusFooter()
                .build();
        carRentalServiceBot.sendMessage(message);
    }

    public void sendNotificationRentalCreated(Rental rental, User user, Car car) {
        String message = MessageBuilder.builder()
                .addRentalCreatedHeader()
                .addId(rental.getId())
                .addUser(user.getEmail())
                .addCar(car.getBrand(), car.getModel())
                .addRentalDate(rental.getRentalDate())
                .addReturnDate(rental.getReturnDate())
                .build();
        carRentalServiceBot.sendMessage(message);
    }

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
        carRentalServiceBot.sendMessage(message);
    }

    public void sendNotificationPaymentSuccess(Payment payment) {
        String message = MessageBuilder.builder()
                .addPaymentCompletedHeader()
                .addId(payment.getId())
                .addTotalAmount(payment.getAmount())
                .addPaymentStatus(payment.getStatus())
                .build();
        carRentalServiceBot.sendMessage(message);
    }

    public void sendNotificationPaymentCancel(Payment payment) {
        String message = MessageBuilder.builder()
                .addPaymentFailedHeader()
                .addId(payment.getId())
                .addTotalAmount(payment.getAmount())
                .addPaymentStatus(payment.getStatus())
                .build();
        carRentalServiceBot.sendMessage(message);
    }

    public void sendNotificationOverdueRentals(Rental rental, long minutes) {
        String message = MessageBuilder.builder()
                .addId(rental.getId())
                .addUser(rental.getUser().getEmail())
                .addRentalDate(rental.getRentalDate())
                .addReturnDate(rental.getReturnDate())
                .addOverdueTime(minutes)
                .build();
        carRentalServiceBot.sendMessage(message);
    }

    public void sendNotificationActiveRentals(Rental rental) {
        String message = MessageBuilder.builder()
                .addId(rental.getId())
                .addUser(rental.getUser().getEmail())
                .addRentalDate(rental.getRentalDate())
                .addReturnDate(rental.getReturnDate())
                .build();
        carRentalServiceBot.sendMessage(message);
    }
}
