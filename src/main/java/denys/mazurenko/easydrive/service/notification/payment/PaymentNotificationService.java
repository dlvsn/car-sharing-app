package denys.mazurenko.easydrive.service.notification.payment;

import denys.mazurenko.easydrive.model.Car;
import denys.mazurenko.easydrive.model.Payment;
import denys.mazurenko.easydrive.model.Rental;
import denys.mazurenko.easydrive.model.User;

public interface PaymentNotificationService {
    void sendNotificationPaymentCreated(Rental rental, User user, Car car, Payment payment);

    void sendNotificationPaymentSuccess(Payment payment);

    void sendNotificationPaymentCancel(Payment payment);
}
