package denys.mazurenko.carsharingapp.service.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

        Rental rental = TestObjectBuilder.initRental();
        when(rentalRepository.findByIdAndUserIdAndActualReturnDateIsNotNull(
                requestDto.rentalId(),
                TEST_USER_ID)
        ).thenReturn(Optional.of(rental));

        when(amountCalculator.calculate(rental)).thenReturn(BigDecimal.valueOf(143.34));

        Session mockSession = mock(Session.class);
        when(stripeService.createRentalPaymentSession(rental, BigDecimal.valueOf(143.34)))
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
                .findByIdAndUserIdAndActualReturnDateIsNotNull(anyLong(), anyLong());
        verify(amountCalculator, times(1))
                .calculate(rental);
        verify(stripeService, times(1))
                .createRentalPaymentSession(any(), any());
        verify(paymentRepository, times(1))
                .save(any(Payment.class));
        verify(paymentNotificationService, times(1))
                .sendNotificationPaymentCreated(any(), any(), any(), any());
        verify(paymentMapper, times(1))
                .toDto(any(Payment.class));
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
                .findByIdAndUserIdAndActualReturnDateIsNotNull(anyLong(), anyLong());
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
                .findByIdAndUserIdAndActualReturnDateIsNotNull(anyLong(), anyLong());
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
                .findByRentalIdFetchRental(anyLong(), anyLong());
        verify(paymentMapper, times(1))
                .toDto(payment);
    }

    @Test
    @DisplayName("""
            Throw EntityNotFoundException when finding payment with non-existing session ID
            """)
    void findPaymentWithNonExistingSessionId_throwsException() {
        when(paymentRepository.findBySessionId(anyString())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () ->
                paymentService.getPaymentStatus(anyString()));
        verify(paymentRepository, times(1))
                .findBySessionId(anyString());
    }

    @Test
    @DisplayName("""
            Throw EntityNotFoundException when finding payment with non-existing rental ID
            """)
    void findPaymentWithNonExistingId_ThrowsException() {
        when(paymentRepository.findByRentalIdFetchRental(99L, TEST_USER_ID))
                .thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () ->
                paymentService.findPaymentById(TestObjectBuilder.initUser(), 99L)
        );

        verify(paymentRepository, times(1))
                .findByRentalIdFetchRental(anyLong(), anyLong());
    }

    @Test
    @DisplayName("""
            Return paid payment status when payment is successful
            """)
    void checkPaymentStatusIfPaid_returnResponseDtoPaid() {
        String sessionId = "test_id";
        Payment payment = TestObjectBuilder.initPayment();

        when(paymentRepository.findBySessionId(sessionId)).thenReturn(Optional.of(payment));

        when(stripeService.checkPaymentStatus(any(String.class))).thenReturn("paid");

        payment.setStatus(Payment.Status.PAID);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        doNothing().when(paymentNotificationService).sendNotificationPaymentSuccess(payment);
        PaymentStatusDto expected = new PaymentStatusDto(payment.getStatus());

        PaymentStatusDto actual = paymentService.getPaymentStatus(sessionId);

        assertThat(expected).isEqualTo(actual);

        verify(paymentRepository, times(1))
                .findBySessionId(anyString());
        verify(stripeService, times(1))
                .checkPaymentStatus(anyString());
        verify(paymentRepository, times(1))
                .save(any(Payment.class));
        verify(paymentNotificationService, times(1))
                .sendNotificationPaymentSuccess(any());
    }

    @Test
    @DisplayName("""
            Return pending payment status when payment is not successful
            """)
    void checkPaymentStatusIfNotPaid_returnResponseDtoPending() {
        String sessionId = "test_id";
        Payment payment = TestObjectBuilder.initPayment();

        when(paymentRepository.findBySessionId(sessionId)).thenReturn(Optional.of(payment));

        when(stripeService.checkPaymentStatus(any(String.class))).thenReturn("any other");

        doNothing().when(paymentNotificationService).sendNotificationPaymentCancel(payment);
        PaymentStatusDto expected = new PaymentStatusDto(payment.getStatus());

        PaymentStatusDto actual = paymentService.getPaymentStatus(sessionId);

        assertThat(expected).isEqualTo(actual);

        verify(paymentRepository, times(1))
                .findBySessionId(anyString());
        verify(stripeService, times(1))
                .checkPaymentStatus(anyString());
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

        when(paymentRepository.findByRentalUserIdFetchRental(user.getId()))
                .thenReturn(List.of(payment));
        List<PaymentResponseDto> expected = List.of(
                TestObjectBuilder.mapPaymentToResponseDto(payment));

        when(paymentMapper.toDto(payment)).thenReturn(expected.get(0));
        List<PaymentResponseDto> actual = paymentService.getPaymentsHistory(user);

        assertThat(expected).isEqualTo(actual);

        verify(paymentRepository, times(1))
                .findByRentalUserIdFetchRental(anyLong());
        verify(paymentMapper, times(1))
                .toDto(payment);
    }
}
