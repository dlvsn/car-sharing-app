package denys.mazurenko.easydrive.exception;

public class StripeSessionFailureException extends RuntimeException {
    public StripeSessionFailureException(String message) {
        super(message);
    }
}
