package denys.mazurenko.carsharingapp.dto.payment;

import denys.mazurenko.carsharingapp.model.Payment;

public record PaymentRequestDto(Long rentalId, Payment.Type type) {
}
