package denys.mazurenko.carsharingapp.mapper;

import denys.mazurenko.carsharingapp.config.MapperConfig;
import denys.mazurenko.carsharingapp.dto.payment.PaymentRequestDto;
import denys.mazurenko.carsharingapp.dto.payment.PaymentResponseDto;
import denys.mazurenko.carsharingapp.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {
    @Mapping(target = "rentalId", source = "rental.id")
    PaymentResponseDto toDto(Payment payment);

    @Mapping(target = "rental", ignore = true)
    Payment toEntity(PaymentRequestDto paymentRequestDto);
}
