package denys.mazurenko.easydrive.exception;

public class CarOutOfStockException extends RuntimeException {
    public CarOutOfStockException(String message) {
        super(message);
    }
}
