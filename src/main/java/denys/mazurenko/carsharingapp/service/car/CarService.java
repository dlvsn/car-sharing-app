package denys.mazurenko.carsharingapp.service.car;

import denys.mazurenko.carsharingapp.dto.car.CarDto;
import denys.mazurenko.carsharingapp.dto.car.UpdateCarRequestDto;
import java.util.List;

public interface CarService {
    CarDto create(CarDto dto);

    List<CarDto> getAllCars();

    CarDto getCarById(Long id);

    CarDto updateCar(Long id, UpdateCarRequestDto dto);

    void deleteCarById(Long id);
}
