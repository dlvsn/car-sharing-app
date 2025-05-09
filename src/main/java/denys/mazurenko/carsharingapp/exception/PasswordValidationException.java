package denys.mazurenko.carsharingapp.exception;

public class PasswordValidationException extends RuntimeException {
    public PasswordValidationException(String message) {
        super(message);
    }
}
