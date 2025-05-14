package denys.mazurenko.easydrive.mapper;

import denys.mazurenko.easydrive.config.MapperConfig;
import denys.mazurenko.easydrive.dto.car.CarDto;
import denys.mazurenko.easydrive.dto.car.UpdateCarRequestDto;
import denys.mazurenko.easydrive.model.Car;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface CarMapper {
    CarDto toDto(Car car);

    Car toEntity(CarDto carDto);

    void updateCarFromDto(UpdateCarRequestDto dto, @MappingTarget Car car);
}
