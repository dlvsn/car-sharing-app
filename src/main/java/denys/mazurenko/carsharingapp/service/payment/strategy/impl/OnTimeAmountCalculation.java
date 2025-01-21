package denys.mazurenko.carsharingapp.service.payment.strategy.impl;

import static java.math.RoundingMode.HALF_UP;

import denys.mazurenko.carsharingapp.model.Rental;
import java.math.BigDecimal;
import java.time.Duration;
import org.springframework.stereotype.Component;

@Component
public class OnTimeAmountCalculation implements CalculationProvider {

    @Override
    public String getKey() {
        return CalculationType.ON_TIME.name();
    }

    @Override
    public BigDecimal getAmount(Rental rental) {
        BigDecimal pricePerMinutes = calculateDailyFeeInMinutes(rental.getCar().getDailyFee());
        long rentingMinutes = Duration.between(
                rental.getRentalDate(), rental.getReturnDate()
        ).toMinutes();
        return pricePerMinutes
                .multiply(BigDecimal.valueOf(rentingMinutes)).setScale(SCALE, HALF_UP);
    }
}
