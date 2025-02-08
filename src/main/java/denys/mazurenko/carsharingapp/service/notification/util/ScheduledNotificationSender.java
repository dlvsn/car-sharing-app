package denys.mazurenko.carsharingapp.service.notification.util;

import static denys.mazurenko.carsharingapp.service.notification.util.MessageBuilder.OVERDUE_HEADER;
import static denys.mazurenko.carsharingapp.service.notification.util.MessageBuilder.START_MESSAGE;

import denys.mazurenko.carsharingapp.model.Rental;
import denys.mazurenko.carsharingapp.repository.RentalRepository;
import denys.mazurenko.carsharingapp.service.notification.rental.RentalNotificationService;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ScheduledNotificationSender {
    private final RentalRepository rentalRepository;
    private final RentalNotificationService notificationService;

    @Scheduled(cron = "0 0 */6 * * *")
    public void sendNotificationActiveRentals() {
        List<Rental> activeRentals = rentalRepository.findAllByActualReturnDateIsNull();
        if (!activeRentals.isEmpty()) {
            notificationService
                    .sendNotificationHeader(
                            START_MESSAGE);
            activeRentals.forEach(notificationService::sendNotificationActiveRentals);
        }
    }

    @Scheduled(cron = "0 5 */6 * * *")
    public void sendNotificationOverdueRentals() {
        List<Rental> activeRentals = rentalRepository.findAllByActualReturnDateIsNull();
        if (!activeRentals.isEmpty()) {
            notificationService.sendNotificationHeader(
                            OVERDUE_HEADER);
            activeRentals
                    .stream()
                    .filter(e -> e.getReturnDate().isBefore(LocalDateTime.now()))
                    .forEach(e -> notificationService
                                            .sendNotificationOverdueRentals(
                                                    e, calculateMinutesRemain(e.getReturnDate())));
        }
    }

    private long calculateMinutesRemain(LocalDateTime endDate) {
        return Duration.between(LocalDateTime.now(), endDate).toMinutes();
    }
}
