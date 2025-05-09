package denys.mazurenko.carsharingapp.service.payment.strategy;

import denys.mazurenko.carsharingapp.model.Rental;
import java.math.BigDecimal;

public interface Calculator {
    BigDecimal calculate(Rental rental);
}
