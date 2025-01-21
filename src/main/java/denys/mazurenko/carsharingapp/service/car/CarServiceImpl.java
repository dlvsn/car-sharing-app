package denys.mazurenko.carsharingapp.service.car;

import denys.mazurenko.carsharingapp.dto.car.CarDto;
import denys.mazurenko.carsharingapp.dto.car.UpdateCarRequestDto;
import denys.mazurenko.carsharingapp.exception.DataProcessingException;
import denys.mazurenko.carsharingapp.exception.EntityNotFoundException;
import denys.mazurenko.carsharingapp.mapper.CarMapper;
import denys.mazurenko.carsharingapp.model.Car;
import denys.mazurenko.carsharingapp.repository.CarRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;

    @Override
    public CarDto save(CarDto dto) {
        if (carRepository
                .existsCarByBrandAndModelAndType(dto.getBrand(), dto.getModel(), dto.getType())) {
            throw new DataProcessingException(
                    String.format(
                            "Car %s %s %s already exists in DB",
                            dto.getBrand(),
                            dto.getModel(),
                            dto.getType()
                    )
            );
        }
        Car newCar = carMapper.toEntity(dto);
        return carMapper.toDto(carRepository.save(newCar));
    }

    @Override
    public List<CarDto> getAllCars() {
        return carRepository.findAll().stream()
                .map(carMapper::toDto)
                .toList();
    }

    @Override
    public CarDto getCarById(Long id) {
        Car car = findCarById(id);
        return carMapper.toDto(car);
    }

    @Override
    public CarDto updateCar(Long id, UpdateCarRequestDto dto) {
        Car car = findCarById(id);
        carMapper.updateCarFromDto(dto, car);
        carRepository.save(car);
        return carMapper.toDto(car);
    }

    @Override
    public void deleteCarById(Long id) {
        carRepository.deleteById(id);
    }

    private Car findCarById(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Can't find car by id " + id
                        )
                );
    }
}
