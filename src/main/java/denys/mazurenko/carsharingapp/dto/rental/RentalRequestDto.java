package denys.mazurenko.carsharingapp.dto.rental;

import jakarta.validation.constraints.Positive;

public record RentalRequestDto(
        @Positive(message = "Car id can't be less than 0")
        Long carId,
        @Positive(message = "Days can't be less than 0")
        int days) {
}
