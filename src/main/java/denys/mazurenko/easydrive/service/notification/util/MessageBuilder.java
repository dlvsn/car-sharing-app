package denys.mazurenko.easydrive.service.notification.util;

import denys.mazurenko.easydrive.model.Payment;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MessageBuilder {
    public static final String PASSWORD_CORRECT = """
                ✅The password is correct.
                📢You have been granted access to receive notifications about rentals and payments.️
                """;
    public static final String START_MESSAGE = "👨‍💻To proceed to the admin panel, enter /manager";
    public static final String GOODBYE_MESSAGE =
            "👋Goodbye ! To start receiving messages again, enter /start";
    public static final String PASSWORD_INCORRECT = "❌Incorrect password. Try again.";
    public static final String OVERDUE_HEADER = "⏰Rental status: Overdue";
    public static final String STATUS_ACTIVE_HEADER = "✅ Rental status: Active";
    public static final String ENTER_PASSWORD = """ 
                        👋Hello, %s
                        Welcome to the car rental admin service 🛠️.
                        Please enter your password to access messages.""";
    public static final String UNKNOWN_COMMAND = "🤷‍♂️Unknown command [ %s ]";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss");
    private final String rentalCreatedHeader = "🏁️ Car Rental was created successfully!";
    private final String rentalCompletedHeader = "🏁️ Car Rental has been completed successfully!";
    private final String paymentSessionCreatedHeader = "💰 Payment session was created:";
    private final String paymentCompletedHeader = "Payment has been completed.";
    private final String paymentFailedHeader = "Payment has been failed.";
    private final String id = "#️⃣ Rental ID: %d";
    private final String user = "👤 User: %s";
    private final String car = "🏎️ Car: %s %s";
    private final String totalAmount = "💶 Total amount: %.2f $";
    private final String rentalDate = "🕖 Rental date: %s";
    private final String returnDate = "🕖 Return date: %s";
    private final String actualReturnDate = "🕖 Actual return date: %s";
    private final String overdueTime = "🕖 Overdue time: %d min";
    private final String paymentStatus = "📌 Status: %s";
    private final String rentalStatusFooter = "✅ Rental status: Completed";

    private MessageBuilder() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private static final String SEPARATOR = "\n";
        private final MessageBuilder messageBuilder = new MessageBuilder();
        private final StringBuilder message = new StringBuilder();

        public Builder addRentalCreatedHeader() {
            message.append(messageBuilder.rentalCreatedHeader).append(SEPARATOR);
            return this;
        }

        public Builder addRentalCompletedHeader() {
            message.append(messageBuilder.rentalCompletedHeader).append(SEPARATOR);
            return this;
        }

        public Builder addPaymentSessionCreatedHeader() {
            message.append(messageBuilder.paymentSessionCreatedHeader).append(SEPARATOR);
            return this;
        }

        public Builder addPaymentCompletedHeader() {
            message.append(messageBuilder.paymentCompletedHeader).append(SEPARATOR);
            return this;
        }

        public Builder addPaymentFailedHeader() {
            message.append(messageBuilder.paymentFailedHeader).append(SEPARATOR);
            return this;
        }

        public Builder addRentalDate(LocalDateTime rentalDate) {
            message.append(String.format(
                    messageBuilder.rentalDate,
                    rentalDate.format(DATE_TIME_FORMATTER))
            ).append(SEPARATOR);
            return this;
        }

        public Builder addReturnDate(LocalDateTime returnDate) {
            message.append(String.format(
                    messageBuilder.returnDate,
                    returnDate.format(DATE_TIME_FORMATTER)
            )).append(SEPARATOR);
            return this;
        }

        public Builder addActualDate(LocalDateTime actualDate) {
            message.append(String.format(
                    messageBuilder.actualReturnDate,
                    actualDate.format(DATE_TIME_FORMATTER)
            )).append(SEPARATOR);
            return this;
        }

        public Builder addId(Long rentalId) {
            message.append(String.format(messageBuilder.id, rentalId)).append(SEPARATOR);
            return this;
        }

        public Builder addUser(String userEmail) {
            message.append(String.format(messageBuilder.user, userEmail)).append(SEPARATOR);
            return this;
        }

        public Builder addCar(String brand, String model) {
            message.append(String.format(messageBuilder.car, brand, model)).append(SEPARATOR);
            return this;
        }

        public Builder addRentalStatusFooter() {
            message.append(messageBuilder.rentalStatusFooter).append(SEPARATOR);
            return this;
        }

        public Builder addTotalAmount(BigDecimal totalAmount) {
            message.append(String.format(
                    messageBuilder.totalAmount, totalAmount)
            ).append(SEPARATOR);
            return this;
        }

        public Builder addPaymentStatus(Payment.Status paymentStatus) {
            message.append(
                    String.format(messageBuilder.paymentStatus, paymentStatus.name())
            ).append(SEPARATOR);
            return this;
        }

        public Builder addOverdueTime(long minutes) {
            message.append(
                    String.format(messageBuilder.overdueTime, minutes)
            ).append(SEPARATOR);
            return this;
        }

        public String build() {
            return message.toString();
        }
    }
}
