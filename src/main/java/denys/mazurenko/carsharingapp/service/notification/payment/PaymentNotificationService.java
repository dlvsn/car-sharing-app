package denys.mazurenko.carsharingapp.service.notification.payment;

import denys.mazurenko.carsharingapp.model.Car;
import denys.mazurenko.carsharingapp.model.Payment;
import denys.mazurenko.carsharingapp.model.Rental;
import denys.mazurenko.carsharingapp.model.User;

public interface PaymentNotificationService {
    void sendNotificationPaymentCreated(Rental rental, User user, Car car, Payment payment);

    void sendNotificationPaymentSuccess(Payment payment);

    void sendNotificationPaymentCancel(Payment payment);
}
