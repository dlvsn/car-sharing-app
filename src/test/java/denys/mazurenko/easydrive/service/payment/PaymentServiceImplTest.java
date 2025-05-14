package denys.mazurenko.easydrive.service.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stripe.model.checkout.Session;
import denys.mazurenko.easydrive.dto.payment.PaymentRequestDto;
import denys.mazurenko.easydrive.dto.payment.PaymentResponseDto;
import denys.mazurenko.easydrive.dto.payment.PaymentStatusDto;
import denys.mazurenko.easydrive.exception.EntityNotFoundException;
import denys.mazurenko.easydrive.mapper.PaymentMapper;
import denys.mazurenko.easydrive.model.Payment;
import denys.mazurenko.easydrive.model.Rental;
import denys.mazurenko.easydrive.model.User;
import denys.mazurenko.easydrive.repository.PaymentRepository;
import denys.mazurenko.easydrive.repository.RentalRepository;
import denys.mazurenko.easydrive.service.notification.payment.PaymentNotificationService;
import denys.mazurenko.easydrive.service.payment.strategy.Calculator;
import denys.mazurenko.easydrive.util.TestObjectBuilder;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
            Successfully create payment session with existing rental and non-null actual return date
            """)
    void createPaymentWithExistingRentalWithNotNullActualReturnDate_Ok() {
        PaymentRequestDto requestDto = new PaymentRequestDto(1L);
        Long rentalDtoId = requestDto.rentalId();

        Rental rental = TestObjectBuilder.initRental();
        when(rentalRepository.findByIdAndUserIdAndActualReturnDateIsNotNull(
                rentalDtoId,
                TEST_USER_ID)
        ).thenReturn(Optional.of(rental));

        BigDecimal amount = BigDecimal.valueOf(143.34);
        when(amountCalculator.calculate(rental)).thenReturn(amount);

        Session mockSession = mock(Session.class);
        when(stripeService.createRentalPaymentSession(rental, amount))
                .thenReturn(mockSession);

        Payment payment = TestObjectBuilder.initPaymentWithSessionFields(mockSession);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        User user = TestObjectBuilder.initUser();

        PaymentResponseDto expected = TestObjectBuilder
                .initPaymentResponseDto(rental, mockSession, payment);
        doNothing().when(paymentNotificationService)
                .sendNotificationPaymentCreated(rental, user, rental.getCar(), payment);
        when(paymentMapper.toDto(payment)).thenReturn(expected);

        PaymentResponseDto actual = paymentService.createPaymentSession(user, requestDto);

        assertThat(expected).isEqualTo(actual);

        verify(rentalRepository, times(1))
                .findByIdAndUserIdAndActualReturnDateIsNotNull(rentalDtoId, TEST_USER_ID);
        verify(amountCalculator, times(1))
                .calculate(rental);
        verify(stripeService, times(1))
                .createRentalPaymentSession(rental, amount);
        verify(paymentRepository, times(1))
                .save(any(Payment.class));
        verify(paymentNotificationService, times(1))
                .sendNotificationPaymentCreated(rental, user, rental.getCar(), payment);
        verify(paymentMapper, times(1))
                .toDto(payment);
    }

    @Test
    @DisplayName("""
            Throw EntityNotFoundException when creating payment with non-existing rental ID
            """)
    void createPaymentWithNonExistingRentalId_ThrowsException() {
        PaymentRequestDto requestDto = new PaymentRequestDto(99L);
        when(rentalRepository
                .findByIdAndUserIdAndActualReturnDateIsNotNull(
                        requestDto.rentalId(), TEST_USER_ID)
        ).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () ->
                paymentService.createPaymentSession(TestObjectBuilder.initUser(), requestDto));
        verify(rentalRepository, times(1))
                .findByIdAndUserIdAndActualReturnDateIsNotNull(requestDto.rentalId(), TEST_USER_ID);
    }

    @Test
    @DisplayName("""
            Throw EntityNotFoundException when creating 
            payment with rental having null actual return date
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
        verify(rentalRepository, times(1))
                .findByIdAndUserIdAndActualReturnDateIsNotNull(rental.getId(), TEST_USER_ID);
    }

    @Test
    @DisplayName("""
            Successfully find payment by ID
            """)
    void findPaymentWithExistingId_Ok() {
        Payment payment = TestObjectBuilder.initPayment();
        User user = TestObjectBuilder.initUser();

        when(paymentRepository.findByRentalIdFetchRental(payment.getId(), TEST_USER_ID))
                .thenReturn(Optional.of(payment));
        PaymentResponseDto expected = TestObjectBuilder.mapPaymentToResponseDto(payment);

        when(paymentMapper.toDto(payment)).thenReturn(expected);
        PaymentResponseDto actual = paymentService.findPaymentById(user, payment.getId());

        assertThat(expected).isEqualTo(actual);

        verify(paymentRepository, times(1))
                .findByRentalIdFetchRental(payment.getId(), TEST_USER_ID);
        verify(paymentMapper, times(1))
                .toDto(payment);
    }

    @Test
    @DisplayName("""
            Throw EntityNotFoundException when finding payment with non-existing session ID
            """)
    void findPaymentWithNonExistingSessionId_throwsException() {
        String invalidSessionId = "invalid_session_id";
        when(paymentRepository.findBySessionId(invalidSessionId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () ->
                paymentService.getPaymentStatus(invalidSessionId));
        verify(paymentRepository, times(1))
                .findBySessionId(invalidSessionId);
    }

    @Test
    @DisplayName("""
            Throw EntityNotFoundException when finding payment with non-existing rental ID
            """)
    void findPaymentWithNonExistingId_ThrowsException() {
        Long invalidId = 99L;
        when(paymentRepository.findByRentalIdFetchRental(invalidId, TEST_USER_ID))
                .thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () ->
                paymentService.findPaymentById(TestObjectBuilder.initUser(), invalidId)
        );

        verify(paymentRepository, times(1))
                .findByRentalIdFetchRental(invalidId, TEST_USER_ID);
    }

    @Test
    @DisplayName("""
            Return paid payment status when payment is successful
            """)
    void checkPaymentStatusIfPaid_returnResponseDtoPaid() {
        Payment payment = TestObjectBuilder.initPayment();
        String sessionId = payment.getSessionId();
        String paymentStatus = String.valueOf(payment.getStatus());

        when(paymentRepository.findBySessionId(sessionId)).thenReturn(Optional.of(payment));

        when(stripeService.checkPaymentStatus(sessionId)).thenReturn("paid");

        payment.setStatus(Payment.Status.PAID);
        when(paymentRepository.save(payment)).thenReturn(payment);
        doNothing().when(paymentNotificationService).sendNotificationPaymentSuccess(payment);
        PaymentStatusDto expected = new PaymentStatusDto(Payment.Status.PAID);

        PaymentStatusDto actual = paymentService.getPaymentStatus(sessionId);

        assertThat(expected).isEqualTo(actual);

        verify(paymentRepository, times(1))
                .findBySessionId(sessionId);
        verify(stripeService, times(1))
                .checkPaymentStatus(sessionId);
        verify(paymentRepository, times(1))
                .save(payment);
        verify(paymentNotificationService, times(1))
                .sendNotificationPaymentSuccess(any());
    }

    @Test
    @DisplayName("""
            Return pending payment status when payment is not successful
            """)
    void checkPaymentStatusIfNotPaid_returnResponseDtoPending() {
        Payment payment = TestObjectBuilder.initPayment();
        String paymentStatus = String.valueOf(payment.getStatus());
        String sessionId = payment.getSessionId();

        when(paymentRepository.findBySessionId(sessionId)).thenReturn(Optional.of(payment));

        when(stripeService.checkPaymentStatus(sessionId)).thenReturn("pending");

        doNothing().when(paymentNotificationService).sendNotificationPaymentCancel(payment);
        PaymentStatusDto expected = new PaymentStatusDto(payment.getStatus());

        PaymentStatusDto actual = paymentService.getPaymentStatus(sessionId);

        assertThat(expected.status()).isEqualTo(actual.status());

        verify(paymentRepository, times(1))
                .findBySessionId(sessionId);
        verify(stripeService, times(1))
                .checkPaymentStatus(sessionId);
        verify(paymentNotificationService, times(1))
                .sendNotificationPaymentCancel(any());
    }

    @Test
    @DisplayName("""
            Successfully get payment history for a user
            """)
    void getPaymentHistory_Success() {
        Payment payment = TestObjectBuilder.initPayment();
        User user = TestObjectBuilder.initUser();

        Long userId = user.getId();
        when(paymentRepository.findByRentalUserIdFetchRental(userId))
                .thenReturn(List.of(payment));
        List<PaymentResponseDto> expected = List.of(
                TestObjectBuilder.mapPaymentToResponseDto(payment));

        when(paymentMapper.toDto(payment)).thenReturn(expected.get(0));
        List<PaymentResponseDto> actual = paymentService.getPaymentsHistory(user);

        assertThat(expected).isEqualTo(actual);

        verify(paymentRepository, times(1))
                .findByRentalUserIdFetchRental(userId);
        verify(paymentMapper, times(1))
                .toDto(payment);
    }
}
