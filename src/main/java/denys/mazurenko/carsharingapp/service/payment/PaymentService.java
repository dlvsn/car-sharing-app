package denys.mazurenko.carsharingapp.service.payment;

import denys.mazurenko.carsharingapp.dto.payment.PaymentRequestDto;
import denys.mazurenko.carsharingapp.dto.payment.PaymentResponseDto;
import denys.mazurenko.carsharingapp.dto.payment.PaymentStatusDto;
import denys.mazurenko.carsharingapp.model.User;
import java.util.List;

public interface PaymentService {
    PaymentResponseDto createPaymentSession(
            User user,
            PaymentRequestDto paymentRequestDto
    );

    PaymentResponseDto findPaymentById(User user, Long rentalId);

    PaymentStatusDto getPaymentStatus(String sessionId);

    List<PaymentResponseDto> getPaymentsHistory(User user);
}
