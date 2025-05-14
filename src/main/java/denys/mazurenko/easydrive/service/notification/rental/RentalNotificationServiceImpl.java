package denys.mazurenko.easydrive.service.notification.rental;

import denys.mazurenko.easydrive.model.Car;
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
public class RentalNotificationServiceImpl implements RentalNotificationService {
    private final RentalServiceBot bot;

    @Override
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
        bot.sendNotification(message);
    }

    @Override
    public void sendNotificationRentalCreated(Rental rental, User user, Car car) {
        String message = MessageBuilder.builder()
                .addRentalCreatedHeader()
                .addId(rental.getId())
                .addUser(user.getEmail())
                .addCar(car.getBrand(), car.getModel())
                .addRentalDate(rental.getRentalDate())
                .addReturnDate(rental.getReturnDate())
                .build();
        bot.sendNotification(message);
    }

    @Override
    public void sendNotificationOverdueRentals(Rental rental, long minutes) {
        String message = MessageBuilder.builder()
                .addId(rental.getId())
                .addUser(rental.getUser().getEmail())
                .addRentalDate(rental.getRentalDate())
                .addReturnDate(rental.getReturnDate())
                .addOverdueTime(minutes)
                .build();
        bot.sendNotification(message);
    }

    @Override
    public void sendNotificationActiveRentals(Rental rental) {
        String message = MessageBuilder.builder()
                .addId(rental.getId())
                .addUser(rental.getUser().getEmail())
                .addRentalDate(rental.getRentalDate())
                .addReturnDate(rental.getReturnDate())
                .build();
        bot.sendNotification(message);
    }

    @Override
    public void sendNotificationHeader(String header) {
        bot.sendNotification(header);
    }
}
