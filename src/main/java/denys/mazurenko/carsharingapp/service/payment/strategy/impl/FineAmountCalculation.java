package denys.mazurenko.carsharingapp.service.payment.strategy.impl;

import static java.math.RoundingMode.HALF_UP;

import denys.mazurenko.carsharingapp.model.Rental;
import java.math.BigDecimal;
import java.time.Duration;
import org.springframework.stereotype.Component;

@Component
public class FineAmountCalculation implements CalculationProvider {

    @Override
    public String getKey() {
        return CalculationType.LATE.name();
    }

    @Override
    public BigDecimal getAmount(Rental rental) {
        BigDecimal finePricePerMinutes = calculateDailyFeeInMinutes(rental.getCar().getDailyFee())
                .multiply(BigDecimal.valueOf(1.5));
        long fineRentingDuration = Duration
                .between(rental.getReturnDate(), rental.getActualReturnDate()).toMinutes();
        return finePricePerMinutes
                .multiply(BigDecimal.valueOf(fineRentingDuration)).setScale(SCALE, HALF_UP);
    }
}
