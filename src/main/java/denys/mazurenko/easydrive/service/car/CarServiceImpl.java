package denys.mazurenko.easydrive.service.car;

import denys.mazurenko.easydrive.dto.car.CarDto;
import denys.mazurenko.easydrive.dto.car.UpdateCarRequestDto;
import denys.mazurenko.easydrive.exception.CarDuplicationException;
import denys.mazurenko.easydrive.exception.EntityNotFoundException;
import denys.mazurenko.easydrive.mapper.CarMapper;
import denys.mazurenko.easydrive.model.Car;
import denys.mazurenko.easydrive.repository.CarRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;

    @Override
    public CarDto create(CarDto dto) {
        if (carRepository
                .existsCarByBrandAndModelAndType(dto.getBrand(), dto.getModel(), dto.getType())) {
            throw new CarDuplicationException(
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
