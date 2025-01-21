package denys.mazurenko.carsharingapp.service.payment;

import com.stripe.model.checkout.Session;
import denys.mazurenko.carsharingapp.dto.payment.PaymentRequestDto;
import denys.mazurenko.carsharingapp.dto.payment.PaymentResponseDto;
import denys.mazurenko.carsharingapp.dto.payment.PaymentStatusDto;
import denys.mazurenko.carsharingapp.exception.EntityNotFoundException;
import denys.mazurenko.carsharingapp.mapper.PaymentMapper;
import denys.mazurenko.carsharingapp.model.Payment;
import denys.mazurenko.carsharingapp.model.Rental;
import denys.mazurenko.carsharingapp.model.User;
import denys.mazurenko.carsharingapp.repository.PaymentRepository;
import denys.mazurenko.carsharingapp.repository.RentalRepository;
import denys.mazurenko.carsharingapp.security.CustomUserDetailsService;
import denys.mazurenko.carsharingapp.service.notification.NotificationService;
import denys.mazurenko.carsharingapp.service.payment.strategy.AmountCalculator;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {
    private static final String IS_PAID = "paid";
    private final AmountCalculator amountCalculator;
    private final NotificationService notificationService;
    private final StripeService stripeService;
    private final PaymentMapper paymentMapper;
    private final PaymentRepository paymentRepository;
    private final CustomUserDetailsService userDetailsService;
    private final RentalRepository rentalRepository;

    @Transactional
    @Override
    public PaymentResponseDto createPaymentSession(
            Authentication authentication,
            PaymentRequestDto paymentRequestDto
    ) {
        User user = userDetailsService.getUserFromAuthentication(authentication);
        Rental rental = rentalRepository
                .findByIdAndUserIdAndActualReturnDateIsNotNull(
                        paymentRequestDto.rentalId(),
                        user.getId()
                )
                .orElseThrow(() ->
                        new EntityNotFoundException("Can't find rental by user id "
                                + user.getId()
                                + " and rental id " + paymentRequestDto.rentalId()
                        )
                );
        Session session = stripeService
                .createRentalPaymentSession(rental, amountCalculator.calculate(rental));
        Payment payment = paymentRepository.save(createPayment(rental, session));
        notificationService.sendNotificationPaymentCreated(rental, user, payment);
        return paymentMapper.toDto(payment);
    }

    @Override
    public PaymentResponseDto findPaymentById(Authentication authentication, Long rentalId) {
        User user = userDetailsService.getUserFromAuthentication(authentication);
        Payment payment = paymentRepository
                .findByRentalIdFetchRental(rentalId, user.getId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Can't find payment by id " + rentalId));
        return paymentMapper.toDto(payment);
    }

    @Override
    public PaymentStatusDto paymentSuccess(String sessionId) {
        Payment payment = getPaymentById(sessionId);

        String status = stripeService.checkPaymentStatus(payment.getSessionId());

        if (status.equals(IS_PAID)) {
            payment.setStatus(Payment.Status.PAID);
            paymentRepository.save(payment);

            notificationService.sendNotificationPaymentSuccess(payment);
            return new PaymentStatusDto(Payment.Status.PAID);
        }
        return new PaymentStatusDto(Payment.Status.PENDING);
    }

    @Override
    public PaymentStatusDto paymentCancel(String sessionId) {
        Payment payment = getPaymentById(sessionId);
        notificationService.sendNotificationPaymentCancel(payment);
        return new PaymentStatusDto(Payment.Status.PENDING);
    }

    @Override
    public List<PaymentResponseDto> getPaymentsHistory(Authentication authentication) {
        User user = userDetailsService.getUserFromAuthentication(authentication);
        return paymentRepository.findByRentalUserIdFetchRental(user.getId())
                .stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    private Payment getPaymentById(String sessionId) {
        return paymentRepository
                .findBySessionId(sessionId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Can't find payment by given session id"));
    }

    private Payment createPayment(Rental rental,
                                  Session session) {
        Payment payment = new Payment();
        payment.setRental(rental);
        payment.setStatus(Payment.Status.PENDING);
        payment.setType(Payment.Type.PAYMENT);
        payment.setSessionId(session.getId());
        payment.setSessionUrl(session.getUrl());
        BigDecimal amount = BigDecimal.valueOf(session.getAmountTotal())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        payment.setAmount(amount);
        return payment;
    }
}
