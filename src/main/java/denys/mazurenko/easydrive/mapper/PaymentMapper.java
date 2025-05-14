package denys.mazurenko.easydrive.mapper;

import denys.mazurenko.easydrive.config.MapperConfig;
import denys.mazurenko.easydrive.dto.payment.PaymentResponseDto;
import denys.mazurenko.easydrive.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {
    @Mapping(target = "rentalId", source = "rental.id")
    @Mapping(target = "paymentType", source = "payment.type")
    PaymentResponseDto toDto(Payment payment);
}
