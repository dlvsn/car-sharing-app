package denys.mazurenko.carsharingapp.dto.car;

import java.math.BigDecimal;

public record UpdateCarRequestDto(
        int inventory,
        BigDecimal dailyFee) {
}
