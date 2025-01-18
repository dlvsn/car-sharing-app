package denys.mazurenko.carsharingapp.exception;

public class CarOutOfStockException extends RuntimeException {
    public CarOutOfStockException(String message) {
        super(message);
    }
}
