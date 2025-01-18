package denys.mazurenko.carsharingapp.service.payment;

import static java.math.RoundingMode.HALF_UP;

import denys.mazurenko.carsharingapp.model.Rental;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class AmountCalculator {
    private static final int MINUTES_IN_ONE_DAY = 1440;
    private static final int SCALE = 2;

    public BigDecimal calculateAmount(Rental rental) {
        BigDecimal dailyFee = rental.getCar().getDailyFee();
        BigDecimal pricePerMinutes = dailyFee
                .divide(BigDecimal.valueOf(MINUTES_IN_ONE_DAY), SCALE, HALF_UP);

        LocalDateTime rentalDate = rental.getRentalDate();
        LocalDateTime returnDate = rental.getReturnDate();
        LocalDateTime actualDate = rental.getActualReturnDate();

        if (actualDate.isBefore(returnDate) || actualDate.isEqual(returnDate)) {
            long rentingMinutes = calculateRentingMinutes(rentalDate, actualDate);
            return pricePerMinutes
                    .multiply(BigDecimal.valueOf(rentingMinutes)).setScale(SCALE, HALF_UP);
        } else {
            long rentingMinutes = calculateRentingMinutes(rentalDate, returnDate);
            long fineRentingMinutes = calculateRentingMinutes(returnDate, actualDate);

            BigDecimal priceDuringRental = pricePerMinutes
                    .multiply(BigDecimal.valueOf(rentingMinutes));

            BigDecimal doublePrice = pricePerMinutes.multiply(BigDecimal.valueOf(2));

            BigDecimal fineRentingSum = doublePrice
                    .multiply(BigDecimal.valueOf(fineRentingMinutes));
            return priceDuringRental.add(fineRentingSum).setScale(SCALE, HALF_UP);
        }
    }

    private long calculateRentingMinutes(LocalDateTime rentalDate, LocalDateTime returnDate) {
        return Duration.between(rentalDate, returnDate).toMinutes();
    }
}
