package denys.mazurenko.easydrive.service.payment;

import com.stripe.model.checkout.Session;
import denys.mazurenko.easydrive.model.Rental;
import java.math.BigDecimal;

public interface StripeService {
    Session createRentalPaymentSession(Rental rental, BigDecimal amount);

    String checkPaymentStatus(String sessionId);
}
