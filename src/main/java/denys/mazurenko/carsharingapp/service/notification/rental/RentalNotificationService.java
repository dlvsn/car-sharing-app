package denys.mazurenko.carsharingapp.service.notification.rental;

import denys.mazurenko.carsharingapp.model.Car;
import denys.mazurenko.carsharingapp.model.Rental;
import denys.mazurenko.carsharingapp.model.User;
import denys.mazurenko.carsharingapp.service.notification.util.MessageBuilder;

public interface RentalNotificationService {
    void sendNotificationRentalCompleted(Rental rental, User user, Car car);

    void sendNotificationRentalCreated(Rental rental, User user, Car car);

    void sendNotificationOverdueRentals(Rental rental, long minutes);

    void sendNotificationActiveRentals(Rental rental);

    void sendNotificationHeader(MessageBuilder.TelegramBotMessageTemplates header);
}
