package denys.mazurenko.carsharingapp.exception;

public class ActiveRentalException extends RuntimeException {
    public ActiveRentalException(String message) {
        super(message);
    }
}
