package denys.mazurenko.carsharingapp.exception;

public class StripeSessionFailureException extends RuntimeException {
    public StripeSessionFailureException(String message) {
        super(message);
    }
}
