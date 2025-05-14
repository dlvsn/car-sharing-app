package denys.mazurenko.easydrive.service.notification.rental;

import denys.mazurenko.easydrive.model.Car;
import denys.mazurenko.easydrive.model.Rental;
import denys.mazurenko.easydrive.model.User;

public interface RentalNotificationService {
    void sendNotificationRentalCompleted(Rental rental, User user, Car car);

    void sendNotificationRentalCreated(Rental rental, User user, Car car);

    void sendNotificationOverdueRentals(Rental rental, long minutes);

    void sendNotificationActiveRentals(Rental rental);

    void sendNotificationHeader(String header);
}
