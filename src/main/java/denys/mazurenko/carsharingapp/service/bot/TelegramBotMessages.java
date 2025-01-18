package denys.mazurenko.carsharingapp.service.bot;

import lombok.Getter;

public class TelegramBotMessages {
    @Getter
    private static final String GREETING_MESSAGE = """
            Hello, Welcome to Car Rental Service.
            """;
    @Getter
    private static final String RENTAL_NOTIFICATION = """
            ✖️➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖✖️
            🏁️Car Rental was created successfully!
            #️⃣ID: %d
            👤User: %s
            🕖Rental date: %s
            🕖Return date:%s
            🏎️Car: %s %s
            ➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖
            """;

    @Getter
    private static final String RENTAL_COMPLETED = """
            ✖️➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖✖️
            🏁️ Car Rental has been completed successfully!
            #️⃣ ID: %d
            👤 User: %s
            🕖 Rental date: %s
            🕖 Return date: %s
            🕖 Actual return date date: %s
            🏎️ Car: %s %s
            ✅ Rental status: Completed
            ➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖
            """;

    @Getter
    private static final String PAYMENT_CREATED = """
            ✖️➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖✖️
            💰Payment session was created:
            #️⃣ ID: %d
            👤 User: %s
            🏎️ Car: %s %s
            🕖 Rental date: %s
            🕖 Return date: %s
            🕖 Actual return date date: %s
            💶 Total amount: %.2f $
            ❌ Status: %s
            ➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖
            """;

    @Getter
    private static final String PAYMENT_PAID = """
            ✖️➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖✖️
            Payment has been completed.
            #️⃣ ID: %d
            💶 Total amount: %.2f $
            ✅ Status: %s
            ➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖
            """;

    @Getter
    private static final String PAYMENT_FAILED = """
            ✖️➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖✖️
            Payment has been completed.
            #️⃣ ID: %d
            💶 Total amount: %.2f $
            ❌ Status: %s
            ➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖
            """;
}
