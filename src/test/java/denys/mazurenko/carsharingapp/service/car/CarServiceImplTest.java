package denys.mazurenko.carsharingapp.service.car;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import denys.mazurenko.carsharingapp.dto.car.CarDto;
import denys.mazurenko.carsharingapp.dto.car.UpdateCarRequestDto;
import denys.mazurenko.carsharingapp.exception.CarDuplicationException;
import denys.mazurenko.carsharingapp.exception.EntityNotFoundException;
import denys.mazurenko.carsharingapp.mapper.CarMapper;
import denys.mazurenko.carsharingapp.model.Car;
import denys.mazurenko.carsharingapp.repository.CarRepository;
import denys.mazurenko.carsharingapp.util.TestObjectBuilder;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CarServiceImplTest {
    @InjectMocks
    private CarServiceImpl carService;

    @Mock
    private CarRepository carRepository;

    @Mock
    private CarMapper carMapper;

    @Test
    @DisplayName("""
            Successfully create a new car when it does not already exist
            """)
    void createNoExistingCar_success() {
        when(carRepository.existsCarByBrandAndModelAndType(any(), any(), any())).thenReturn(false);

        Car car = TestObjectBuilder.initCar();
        CarDto dto = TestObjectBuilder.mapCarToDto(car);

        when(carMapper.toEntity(dto)).thenReturn(car);
        when(carRepository.save(car)).thenReturn(car);

        when(carMapper.toDto(car)).thenReturn(dto);
        CarDto actual = carService.create(dto);

        assertThat(dto).isEqualTo(actual);

        verify(carRepository, times(1))
                .existsCarByBrandAndModelAndType(any(), any(), any());
        verify(carMapper, times(1))
                .toEntity(dto);
        verify(carRepository, times(1))
                .save(car);
        verify(carMapper, times(1))
                .toDto(car);
    }

    @Test
    @DisplayName("""
            Throw CarDuplicationException when trying to create an existing car
            """)
    void createExistingCar_throwsException() {
        when(carRepository.existsCarByBrandAndModelAndType(any(), any(), any())).thenReturn(true);
        assertThrows(CarDuplicationException.class, () -> carService.create(new CarDto()));
        verify(carRepository, times(1))
                .existsCarByBrandAndModelAndType(any(), any(), any());
    }

    @Test
    @DisplayName("""
            Successfully get all cars
            """)
    void getAllCars_success() {
        Car car = TestObjectBuilder.initCar();
        when(carRepository.findAll()).thenReturn(List.of(car));
        CarDto expected = TestObjectBuilder.mapCarToDto(car);
        when(carMapper.toDto(car)).thenReturn(expected);
        List<CarDto> actual = carService.getAllCars();

        assertThat(actual).hasSize(1);
        assertThat(List.of(expected)).isEqualTo(actual);

        verify(carRepository, times(1))
                .findAll();
        verify(carMapper, times(1))
                .toDto(car);
    }

    @Test
    @DisplayName("""
            Successfully get car by ID when it exists
            """)
    void getCarWithExistingId_success() {
        Car car = TestObjectBuilder.initCar();
        when(carRepository.findById(anyLong())).thenReturn(Optional.of(car));

        CarDto expected = TestObjectBuilder.mapCarToDto(car);
        when(carMapper.toDto(car)).thenReturn(expected);

        CarDto actual = carService.getCarById(car.getId());
        assertThat(actual).isEqualTo(expected);

        verify(carRepository, times(1))
                .findById(anyLong());
        verify(carMapper, times(1))
                .toDto(car);
    }

    @Test
    @DisplayName("""
            Throw EntityNotFoundException when car with ID does not exist
            """)
    void getCarWithNoExistingId_throwsException() {
        when(carRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> carService.getCarById(99L));
        verify(carRepository, times(1))
                .findById(99L);
    }

    @Test
    @DisplayName("""
            Successfully update car when car exists
            """)
    void updateCar_success() {
        Car car = TestObjectBuilder.initCar();
        UpdateCarRequestDto dto = TestObjectBuilder.initUpdateCarDto();
        when(carRepository.findById(anyLong())).thenReturn(Optional.of(car));
        doNothing().when(carMapper).updateCarFromDto(dto, car);

        when(carRepository.save(any(Car.class))).thenReturn(car);
        CarDto expected = TestObjectBuilder.mapCarToDto(car);
        when(carMapper.toDto(any(Car.class))).thenReturn(expected);

        CarDto actual = carService.updateCar(anyLong(), dto);

        assertThat(actual).isEqualTo(expected);

        verify(carRepository, times(1))
                .findById(anyLong());
        verify(carMapper, times(1))
                .updateCarFromDto(dto, car);
        verify(carRepository, times(1))
                .save(car);
        verify(carMapper, times(1))
                .toDto(car);
    }

    @Test
    @DisplayName("""
            Throw EntityNotFoundException when trying to update car with non-existing ID
            """)
    void updateCarWithNoExistingId_throwsException() {
        when(carRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () ->
                carService.updateCar(anyLong(), TestObjectBuilder.initUpdateCarDto()));

        verify(carRepository, times(1))
                .findById(anyLong());
    }
}
