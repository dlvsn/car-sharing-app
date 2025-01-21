package denys.mazurenko.carsharingapp.service.notification;

import denys.mazurenko.carsharingapp.model.Car;
import denys.mazurenko.carsharingapp.model.Payment;
import denys.mazurenko.carsharingapp.model.Rental;
import denys.mazurenko.carsharingapp.model.User;
import java.time.format.DateTimeFormatter;
import lombok.Getter;

public class TelegramBotMessages {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss");

    @Getter
    private static final String GREETING_MESSAGE = """
            👨‍💻To proceed to the admin panel, enter anything
            """;
    @Getter
    private static final String PASSWORD_CORRECT = """
            ✅The password is correct.
            📢You have been granted access to receive notifications about rentals and payments.️
            """;
    @Getter
    private static final String PASSWORD_INCORRECT = """
            ❌Incorrect password. Try again.
            """;
    @Getter
    private static final String GOODBYE_MESSAGE = """
            👋Goodbye ! To start receiving messages again, enter /start
            """;
    @Getter
    private static final String STATUS_ACTIVE_HEADER = """
            ✅ Rental status: Active
            """;
    @Getter
    private static final String OVERDUE_HEADER = """
            ⏰Rental status: Overdue
            """;

    public static String enterPassword(String name) {
        return String.format("""
            👋Hello, %s
            Welcome to the car rental admin service 🛠️.
            Please enter your password to access messages.
            """, name
        );
    }

    public static String rentalNotification(Rental rental, User user, Car car) {
        return String.format(
                """
                        🏁️Car Rental was created successfully!
                        #️⃣ID: %d
                        👤User: %s
                        🕖Rental date: %s
                        🕖Return date:%s
                        🏎️Car: %s %s
                        """,
                rental.getId(),
                user.getEmail(),
                rental.getRentalDate().format(DATE_TIME_FORMATTER),
                rental.getReturnDate().format(DATE_TIME_FORMATTER),
                car.getBrand(), car.getModel()
        );
    }

    public static String rentalCompleted(Rental rental, User user, Car car) {
        return String.format(
                """
            🏁️ Car Rental has been completed successfully!
            #️⃣ ID: %d
            👤 User: %s
            🕖 Rental date: %s
            🕖 Return date: %s
            🕖 Actual return date date: %s
            🏎️ Car: %s %s
            ✅ Rental status: Completed
            """,
                rental.getId(),
                user.getEmail(),
                rental.getRentalDate(),
                rental.getReturnDate(),
                rental.getActualReturnDate(),
                car.getBrand(), car.getModel()
        );
    }

    public static String activeRentals(Rental rental, long minutesRemain) {
        return String.format("""
            #️⃣ ID: %d
            🕖 Rental date: %s
            🕖 Return date: %s
            🕖 Time remaining until the end of the rental: %d
            """,
                rental.getId(),
                rental.getRentalDate(),
                rental.getReturnDate(),
                minutesRemain
        );
    }

    public static String overdueRentals(Rental rental, long overdueMinutes) {
        return String.format("""
            #️⃣ ID: %d
            🕖 Rental date: %s
            🕖 Return date: %s
            🕖 Overdue time: %d
            """,
                rental.getId(),
                rental.getRentalDate(),
                rental.getReturnDate(),
                overdueMinutes
        );
    }

    public static String paymentCreated(Rental rental, User user, Payment payment) {
        return String.format(
                """
            💰Payment session was created:
            #️⃣ Rental ID: %d
            👤 User: %s
            🏎️ Car: %s %s
            🕖 Rental date: %s
            🕖 Return date: %s
            🕖 Actual return date date: %s
            💶 Total amount: %.2f $
            ❌ Status: %s
            """,
                rental.getId(),
                user.getEmail(),
                rental.getCar().getBrand(),
                rental.getCar().getModel(),
                rental.getRentalDate(),
                rental.getReturnDate(),
                rental.getActualReturnDate(),
                payment.getAmount(),
                payment.getStatus()
        );
    }

    public static String paymentPaid(Payment payment) {
        return String.format("""
            Payment has been completed.
            #️⃣ ID: %d
            💶 Total amount: %.2f $
            ✅ Status: %s
            """,
                payment.getId(),
                payment.getAmount(),
                payment.getStatus()
        );
    }

    public static String paymentFailed(Payment payment) {
        return String.format("""
            Payment has been failed.
            #️⃣ ID: %d
            💶 Total amount: %.2f $
            ✅ Status: %s
            """,
                payment.getId(),
                payment.getAmount(),
                payment.getStatus()
        );
    }
}
