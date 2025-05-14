package denys.mazurenko.easydrive.mapper;

import denys.mazurenko.easydrive.config.MapperConfig;
import denys.mazurenko.easydrive.dto.rental.RentalResponseDto;
import denys.mazurenko.easydrive.model.Rental;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface RentalMapper {
    @Mapping(target = "carId", source = "car.id")
    RentalResponseDto toDto(Rental rental);
}
