package denys.mazurenko.easydrive.service.payment.strategy;

import denys.mazurenko.easydrive.model.Rental;
import denys.mazurenko.easydrive.service.payment.strategy.impl.CalculationProviderManager;
import denys.mazurenko.easydrive.service.payment.strategy.impl.CalculationType;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AmountCalculator implements Calculator {
    private final CalculationProviderManager calculationProviderManager;

    @Override
    public BigDecimal calculate(Rental rental) {
        BigDecimal amount = calculationProviderManager
                .getCalculationProvider(CalculationType.ON_TIME.name()).getAmount(rental);
        if (rental.getActualReturnDate().isAfter(rental.getReturnDate())) {
            BigDecimal fineAmount = calculationProviderManager
                    .getCalculationProvider(CalculationType.LATE.name()).getAmount(rental);
            amount = amount.add(fineAmount);
        }
        return amount;
    }
}
