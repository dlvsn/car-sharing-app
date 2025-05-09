package denys.mazurenko.carsharingapp.exception;

public class TelegramBotInitializationException extends RuntimeException {
    public TelegramBotInitializationException(String message) {
        super(message);
    }
}
