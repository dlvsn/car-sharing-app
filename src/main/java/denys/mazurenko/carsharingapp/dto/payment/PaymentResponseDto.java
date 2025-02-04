package denys.mazurenko.carsharingapp.dto.payment;

import denys.mazurenko.carsharingapp.model.Payment;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PaymentResponseDto {
    private Long rentalId;
    private String sessionId;
    private String sessionUrl;
    private BigDecimal amount;
    private Payment.Type paymentType;
    private Payment.Status status;
}
