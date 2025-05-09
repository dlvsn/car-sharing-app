package denys.mazurenko.carsharingapp.dto.payment;

import denys.mazurenko.carsharingapp.model.Payment;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentResponseDto {
    private Long rentalId;
    private String sessionId;
    private String sessionUrl;
    private BigDecimal amount;
    private Payment.Type paymentType;
    private Payment.Status status;
}
