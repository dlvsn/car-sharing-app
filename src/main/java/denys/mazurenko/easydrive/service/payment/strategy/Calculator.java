package denys.mazurenko.easydrive.service.payment.strategy;

import denys.mazurenko.easydrive.model.Rental;
import java.math.BigDecimal;

public interface Calculator {
    BigDecimal calculate(Rental rental);
}
