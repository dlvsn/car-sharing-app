package denys.mazurenko.carsharingapp.service.car;

import java.util.List;
import denys.mazurenko.carsharingapp.dto.car.CarDto;
import denys.mazurenko.carsharingapp.model.Car;

public interface CarService {
    CarDto save(Car dto);

    List<CarDto> getAllCars();
}
