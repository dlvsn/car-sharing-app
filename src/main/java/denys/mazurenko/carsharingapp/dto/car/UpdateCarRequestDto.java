package denys.mazurenko.carsharingapp.dto.car;

import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record UpdateCarRequestDto(
        @Positive(message = "Inventory can't be less than 0")
        int inventory,
        @Positive(message = "Daily fee can't be less than 0")
        BigDecimal dailyFee) {
}
