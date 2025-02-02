package denys.mazurenko.carsharingapp.dto.payment;

import denys.mazurenko.carsharingapp.model.Payment;
import lombok.Setter;

@Setter
public class PaymentStatusDto {
    private Payment.Status status;
}
