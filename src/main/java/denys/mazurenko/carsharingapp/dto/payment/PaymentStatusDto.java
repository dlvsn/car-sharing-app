package denys.mazurenko.carsharingapp.dto.payment;

import denys.mazurenko.carsharingapp.model.Payment;

public record PaymentStatusDto(Payment.Status status) {
}
