package denys.mazurenko.carsharingapp.exception;

import denys.mazurenko.carsharingapp.dto.exception.ErrorResponseDto;
import java.util.List;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomGlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationExceptions(
            MethodArgumentNotValidException exception) {
        List<String> errors = exception.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();
        return new ResponseEntity<>(
                new ErrorResponseDto(exception.getClass().getSimpleName(),
                        errors, HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataProcessingException.class)
    public ResponseEntity<ErrorResponseDto> handleDataProcessingException(
            DataProcessingException exception) {
        return new ResponseEntity<>(initErrorDto(exception, HttpStatus.INTERNAL_SERVER_ERROR),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(java.sql.SQLException.class)
    public ResponseEntity<ErrorResponseDto> handleException(Exception exception) {
        return new ResponseEntity<>(initErrorDto(exception, HttpStatus.INTERNAL_SERVER_ERROR),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleEntityNotFoundException(
            EntityNotFoundException exception) {
        return new ResponseEntity<>(initErrorDto(exception, HttpStatus.NOT_FOUND),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RegistrationException.class)
    public ResponseEntity<ErrorResponseDto> handleRegistrationException(
            RegistrationException exception) {
        return new ResponseEntity<>(initErrorDto(exception, HttpStatus.CONFLICT),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler(PasswordValidationException.class)
    public ResponseEntity<ErrorResponseDto> handlePasswordValidationException(
            PasswordValidationException exception) {
        return new ResponseEntity<>(initErrorDto(exception, HttpStatus.BAD_REQUEST),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ActiveRentalException.class)
    public ResponseEntity<ErrorResponseDto> handleActiveRentalException(
            ActiveRentalException exception) {
        return new ResponseEntity<>(initErrorDto(exception, HttpStatus.CONFLICT),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler(CarOutOfStockException.class)
    public ResponseEntity<ErrorResponseDto> handleCarOutOfStockException(
            CarOutOfStockException exception) {
        return new ResponseEntity<>(initErrorDto(exception, HttpStatus.NOT_FOUND),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CarDuplicationException.class)
    public ResponseEntity<ErrorResponseDto> handleCarDuplicationException(
            CarDuplicationException exception) {
        return new ResponseEntity<>(initErrorDto(exception, HttpStatus.CONFLICT),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler(StripeSessionFailureException.class)
    public ResponseEntity<ErrorResponseDto> handleStripeSessionFailureException(
            StripeSessionFailureException exception) {
        return new ResponseEntity<>(initErrorDto(exception, HttpStatus.INTERNAL_SERVER_ERROR),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private <T extends Exception> ErrorResponseDto initErrorDto(T exception, HttpStatus status) {
        return new ErrorResponseDto(
                exception.getClass().getSimpleName(),
                List.of(exception.getMessage()),
                status
        );
    }
}
