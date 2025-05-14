package denys.mazurenko.easydrive.service.payment;

import denys.mazurenko.easydrive.dto.payment.PaymentRequestDto;
import denys.mazurenko.easydrive.dto.payment.PaymentResponseDto;
import denys.mazurenko.easydrive.dto.payment.PaymentStatusDto;
import denys.mazurenko.easydrive.model.User;
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
