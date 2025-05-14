package denys.mazurenko.easydrive.service.car;

import denys.mazurenko.easydrive.dto.car.CarDto;
import denys.mazurenko.easydrive.dto.car.UpdateCarRequestDto;
import java.util.List;

public interface CarService {
    CarDto create(CarDto dto);

    List<CarDto> getAllCars();

    CarDto getCarById(Long id);

    CarDto updateCar(Long id, UpdateCarRequestDto dto);

    void deleteCarById(Long id);
}
