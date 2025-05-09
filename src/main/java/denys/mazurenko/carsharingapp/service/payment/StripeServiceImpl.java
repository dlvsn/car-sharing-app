package denys.mazurenko.carsharingapp.service.payment;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import denys.mazurenko.carsharingapp.exception.StripeSessionFailureException;
import denys.mazurenko.carsharingapp.model.Rental;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class StripeServiceImpl implements StripeService {
    private static final String STRIPE_ERROR_MESSAGE = "Can't create stripe session: %s";
    private static final String USD = "usd";
    private static final Long DEFAULT_QUANTITY = 1L;
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

    @Override
    public Session createRentalPaymentSession(Rental rental, BigDecimal amount) {
        SessionCreateParams sessionParams = createSessionParams(amount, rental);
        return createSession(sessionParams);
    }

    @Override
    public String checkPaymentStatus(String sessionId) {
        try {
            Session session = Session.retrieve(sessionId);
            return session.getPaymentStatus();
        } catch (StripeException e) {
            throw new StripeSessionFailureException(String.format(
                    STRIPE_ERROR_MESSAGE,
                    e.getMessage()
            ));
        }
    }

    private Session createSession(SessionCreateParams params) {
        try {
            return Session.create(params);
        } catch (StripeException e) {
            throw new StripeSessionFailureException(String.format(
                    STRIPE_ERROR_MESSAGE,
                    e.getMessage()
            ));
        }
    }

    private SessionCreateParams createSessionParams(BigDecimal amount, Rental rental) {
        return SessionCreateParams
                .builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addLineItem(createLineItem(amount, rental))
                .build();
    }

    private SessionCreateParams.LineItem createLineItem(BigDecimal amount, Rental rental) {
        return SessionCreateParams
                .LineItem
                .builder()
                .setQuantity(DEFAULT_QUANTITY)
                .setPriceData(createPriceData(amount, rental))
                .build();
    }

    private SessionCreateParams.LineItem.PriceData createPriceData(BigDecimal amount,
                                                                   Rental rental) {
        return SessionCreateParams
                .LineItem
                .PriceData
                .builder()
                .setCurrency(USD)
                .setUnitAmountDecimal(amount.multiply(BigDecimal.valueOf(100)))
                .setProductData(createProductData(rental))
                .build();
    }

    private SessionCreateParams.LineItem.PriceData.ProductData createProductData(Rental rental) {
        return SessionCreateParams
                .LineItem
                .PriceData
                .ProductData
                .builder()
                .setName(
                        "Renting car: "
                                + rental.getCar()
                                .getBrand()
                                + " "
                                + rental.getCar()
                                .getModel())
                .build();
    }
}
