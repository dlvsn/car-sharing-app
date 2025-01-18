package denys.mazurenko.carsharingapp.service.payment;

import denys.mazurenko.carsharingapp.dto.payment.PaymentRequestDto;
import denys.mazurenko.carsharingapp.dto.payment.PaymentResponseDto;
import denys.mazurenko.carsharingapp.dto.payment.PaymentStatusDto;
import java.util.List;
import org.springframework.security.core.Authentication;

public interface PaymentService {
    PaymentResponseDto createPaymentSession(
            Authentication authentication,
            PaymentRequestDto paymentRequestDto
    );

    PaymentResponseDto findPaymentById(Authentication authentication, Long rentalId);

    PaymentStatusDto paymentSuccess(String sessionId);

    PaymentStatusDto paymentCancel(String sessionId);

    List<PaymentResponseDto> getPaymentsHistory(Authentication authentication);
}
