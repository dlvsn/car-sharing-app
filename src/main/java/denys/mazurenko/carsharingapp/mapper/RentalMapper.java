package denys.mazurenko.carsharingapp.mapper;

import denys.mazurenko.carsharingapp.config.MapperConfig;
import denys.mazurenko.carsharingapp.dto.rental.RentalResponseDto;
import denys.mazurenko.carsharingapp.model.Rental;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface RentalMapper {
    @Mapping(target = "carId", source = "car.id")
    RentalResponseDto toDto(Rental rental);
}
