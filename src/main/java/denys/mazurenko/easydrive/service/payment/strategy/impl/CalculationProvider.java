package denys.mazurenko.easydrive.service.payment.strategy.impl;

import static java.math.RoundingMode.HALF_UP;

import denys.mazurenko.easydrive.model.Rental;
import java.math.BigDecimal;

public interface CalculationProvider {
    int MINUTES_IN_ONE_DAY = 1440;

    int SCALE = 2;

    String getKey();

    BigDecimal getAmount(Rental rental);

    default BigDecimal calculateDailyFeeInMinutes(BigDecimal amount) {
        return amount
                .divide(BigDecimal.valueOf(MINUTES_IN_ONE_DAY), SCALE, HALF_UP);
    }
}
