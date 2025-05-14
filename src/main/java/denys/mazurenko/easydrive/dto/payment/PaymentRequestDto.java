package denys.mazurenko.easydrive.dto.payment;

import jakarta.validation.constraints.Positive;

public record PaymentRequestDto(
        @Positive(message = "Rental id can't be less than 0")
        Long rentalId) {
}
