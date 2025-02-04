package denys.mazurenko.carsharingapp.service.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
import denys.mazurenko.carsharingapp.util.TestObjectBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {
    private static final Long TEST_USER_ID = 2L;
    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Mock
    private Calculator amountCalculator;

    @Mock
    private StripeService stripeService;

    @Mock
    private PaymentNotificationService paymentNotificationService;

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private RentalRepository rentalRepository;


    @Test
    @DisplayName("""
            """)
    void createPaymentWithExistingRentalWithNotNullActualReturnDate_Ok() {
        PaymentRequestDto requestDto = new PaymentRequestDto(1L);

        Rental rental = TestObjectBuilder.initRental();
        when(rentalRepository.findByIdAndUserIdAndActualReturnDateIsNotNull(
                requestDto.rentalId(),
                TEST_USER_ID)
        ).thenReturn(Optional.of(rental));

        when(amountCalculator.calculate(rental)).thenReturn(BigDecimal.valueOf(143.34));

        Session mockSession = mock(Session.class);
        when(stripeService.createRentalPaymentSession(rental, BigDecimal.valueOf(143.34))).thenReturn(mockSession);

        Payment payment = TestObjectBuilder.initPaymentWithSessionFields(mockSession);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        User user = TestObjectBuilder.initUser();

        PaymentResponseDto expected = TestObjectBuilder.initPaymentResponseDto(rental, mockSession, payment);

        when(paymentMapper.toDto(payment)).thenReturn(expected);

        PaymentResponseDto actual = paymentService.createPaymentSession(user, requestDto);

        assertThat(expected).isEqualTo(actual);
    }

    @Test
    @DisplayName("""
            """)
    void createPaymentWithNonExistingRentalId_ThrowsException() {
        PaymentRequestDto requestDto = new PaymentRequestDto(99L);
        when(rentalRepository
                .findByIdAndUserIdAndActualReturnDateIsNotNull(
                        requestDto.rentalId(), TEST_USER_ID)
        ).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () ->
                paymentService.createPaymentSession(TestObjectBuilder.initUser(), requestDto));
    }

    @Test
    @DisplayName("""
            """)
    void createPaymentRentalWithNullActualReturnDate_ThrowsException() {
        Rental rental = TestObjectBuilder.initRental();
        rental.setActualReturnDate(null);

        PaymentRequestDto requestDto = new PaymentRequestDto(1L);
        when(rentalRepository.findByIdAndUserIdAndActualReturnDateIsNotNull(
                rental.getId(),
                TEST_USER_ID)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () ->
                paymentService.createPaymentSession(TestObjectBuilder.initUser(), requestDto));
    }

    @Test
    @DisplayName("""
            """)
    void findPaymentWithExistingId_Ok() {
        Payment payment = TestObjectBuilder.initPayment();
        User user = TestObjectBuilder.initUser();

        when(paymentRepository.findByRentalIdFetchRental(payment.getId(), TEST_USER_ID)).thenReturn(Optional.of(payment));
        PaymentResponseDto expected = TestObjectBuilder.mapPaymentToResponseDto(payment);

        when(paymentMapper.toDto(payment)).thenReturn(expected);
        PaymentResponseDto actual = paymentService.findPaymentById(user, payment.getId());

        assertThat(expected).isEqualTo(actual);
    }

    @Test
    @DisplayName("""
            """)
    void findPaymentWithNonExistingId_ThrowsException() {
        when(paymentRepository.findByRentalIdFetchRental(99L, TEST_USER_ID)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () ->
                paymentService.findPaymentById(TestObjectBuilder.initUser(), 99L)
        );
    }

    @Test
    @DisplayName("""
            """)
    void checkPaymentStatusIfPaid_returnResponseDtoPaid() {
        String sessionId = "test_id";
        Payment payment = TestObjectBuilder.initPayment();

        when(paymentRepository.findBySessionId(sessionId)).thenReturn(Optional.of(payment));

        when(stripeService.checkPaymentStatus(any(String.class))).thenReturn("paid");

        payment.setStatus(Payment.Status.PAID);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        PaymentStatusDto expected = new PaymentStatusDto(payment.getStatus());

        PaymentStatusDto actual = paymentService.getPaymentStatus(sessionId);

        assertThat(expected).isEqualTo(actual);
    }

    @Test
    @DisplayName("""
            """)
    void checkPaymentStatusIfNotPaid_returnResponseDtoPending() {
        String sessionId = "test_id";
        Payment payment = TestObjectBuilder.initPayment();

        when(paymentRepository.findBySessionId(sessionId)).thenReturn(Optional.of(payment));

        when(stripeService.checkPaymentStatus(any(String.class))).thenReturn("any other");

        PaymentStatusDto expected = new PaymentStatusDto(payment.getStatus());

        PaymentStatusDto actual = paymentService.getPaymentStatus(sessionId);

        assertThat(expected).isEqualTo(actual);
    }

    @Test
    @DisplayName("""
            """)
    void getPaymentHistory_Success() {
        Payment payment = TestObjectBuilder.initPayment();
        User user = TestObjectBuilder.initUser();

        when(paymentRepository.findByRentalUserIdFetchRental(user.getId())).thenReturn(List.of(payment));
        List<PaymentResponseDto> expected = List.of(TestObjectBuilder.mapPaymentToResponseDto(payment));

        when(paymentMapper.toDto(payment)).thenReturn(expected.get(0));
        List<PaymentResponseDto> actual = paymentService.getPaymentsHistory(user);

        assertThat(expected).isEqualTo(actual);
    }
}