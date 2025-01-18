package denys.mazurenko.carsharingapp.service.payment;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import denys.mazurenko.carsharingapp.model.Rental;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class StripeService {
    private static final String USD = "usd";
    private static final Long DEFAULT_QUANTITY = 1L;
    private final AmountCalculator amountCalculator;
    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Value("${stripe.success.url}")
    private String successUrl;

    @Value("${stripe.cancel.url}")
    private String cancelUrl;

    @PostConstruct
    void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    public Session createRentalPaymentSession(Rental rental) {
        SessionCreateParams params = SessionCreateParams
                .builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addLineItem(
                        SessionCreateParams
                                .LineItem.builder()
                                .setQuantity(DEFAULT_QUANTITY)
                                .setPriceData(
                                        SessionCreateParams
                                                .LineItem
                                                .PriceData.builder()
                                                .setCurrency(USD)
                                                .setUnitAmountDecimal(
                                                        amountCalculator
                                                                .calculateAmount(rental)
                                                                .multiply(BigDecimal.valueOf(100)))
                                                .setProductData(
                                                        SessionCreateParams
                                                                .LineItem.PriceData
                                                                .ProductData.builder()
                                                                .setName(
                                                                        "Renting car: "
                                                                        + rental.getCar()
                                                                                .getBrand()
                                                                        + " "
                                                                        + rental.getCar()
                                                                                .getModel())
                                                                .build()
                                                ).build()
                                ).build()
                )
                .build();
        try {
            return Session.create(params);
        } catch (StripeException e) {
            throw new RuntimeException(
                    String.format(
                            "Can't create stripe session: %s",
                            e.getMessage()
                    )
            );
        }
    }

    public String checkPaymentStatus(String sessionId) {
        try {
            Session session = Session.retrieve(sessionId);
            return session.getPaymentStatus();
        } catch (StripeException e) {
            throw new RuntimeException(String.format(
                    "Can't create stripe session: %s",
                    e.getMessage()
            ));
        }
    }
}
