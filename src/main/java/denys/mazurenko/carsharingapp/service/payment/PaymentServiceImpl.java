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
import denys.mazurenko.carsharingapp.service.notification.payment.PaymentNotificationService;
import denys.mazurenko.carsharingapp.service.payment.strategy.Calculator;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {
    private static final String PAID = "paid";
    private final Calculator amountCalculator;
    private final PaymentNotificationService paymentNotificationService;
    private final StripeService stripeService;
    private final PaymentMapper paymentMapper;
    private final PaymentRepository paymentRepository;
    private final RentalRepository rentalRepository;

    @Override
    public PaymentResponseDto createPaymentSession(
            User user,
            PaymentRequestDto paymentRequestDto
    ) {
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
        paymentNotificationService.sendNotificationPaymentCreated(
                rental,
                user,
                rental.getCar(),
                payment
        );
        return paymentMapper.toDto(payment);
    }

    @Override
    public PaymentResponseDto findPaymentById(User user, Long rentalId) {
        Payment payment = paymentRepository
                .findByRentalIdFetchRental(rentalId, user.getId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Can't find payment by id " + rentalId));
        return paymentMapper.toDto(payment);
    }

    @Override
    public PaymentStatusDto getPaymentStatus(String sessionId) {
        Payment payment = getPaymentById(sessionId);

        String status = stripeService.checkPaymentStatus(payment.getSessionId());

        if (status.equals(PAID)) {
            payment.setStatus(Payment.Status.PAID);
            paymentRepository.save(payment);
            paymentNotificationService.sendNotificationPaymentSuccess(payment);
            return new PaymentStatusDto(Payment.Status.PAID);
        } else {
            paymentNotificationService.sendNotificationPaymentCancel(payment);
        }
        return new PaymentStatusDto(Payment.Status.PENDING);
    }

    @Override
    public List<PaymentResponseDto> getPaymentsHistory(User user) {
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
