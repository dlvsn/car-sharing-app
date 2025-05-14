package denys.mazurenko.easydrive.service.rental;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import denys.mazurenko.easydrive.dto.rental.RentalRequestDto;
import denys.mazurenko.easydrive.dto.rental.RentalResponseDto;
import denys.mazurenko.easydrive.exception.ActiveRentalException;
import denys.mazurenko.easydrive.exception.CarOutOfStockException;
import denys.mazurenko.easydrive.exception.EntityNotFoundException;
import denys.mazurenko.easydrive.mapper.RentalMapper;
import denys.mazurenko.easydrive.model.Car;
import denys.mazurenko.easydrive.model.Rental;
import denys.mazurenko.easydrive.model.User;
import denys.mazurenko.easydrive.repository.CarRepository;
import denys.mazurenko.easydrive.repository.RentalRepository;
import denys.mazurenko.easydrive.service.notification.rental.RentalNotificationService;
import denys.mazurenko.easydrive.util.TestObjectBuilder;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RentalServiceImplTest {
    @InjectMocks
    private RentalServiceImpl rentalService;

    @Mock
    private RentalNotificationService rentalNotificationService;

    @Mock
    private CarRepository carRepository;

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private RentalMapper rentalMapper;

    @Test
    @DisplayName("""
            Successfully rent a car with valid car ID and inventory greater than zero
            """)
    void rentCarWithValidCarIdAndInventoryBiggerThanZero_success() {
        User user = TestObjectBuilder.initUser();
        when(rentalRepository.findByUserIdAndActualReturnDateIsNull(user.getId()))
                .thenReturn(Optional.empty());

        Car car = TestObjectBuilder.initCar();
        when(carRepository.findById(car.getId())).thenReturn(Optional.of(car));

        Rental rental = TestObjectBuilder.initRental();

        when(carRepository.save(car)).thenReturn(car);
        when(rentalRepository.save(any(Rental.class))).thenReturn(rental);
        doNothing().when(rentalNotificationService)
                .sendNotificationRentalCreated(any(), any(), any());

        RentalResponseDto expected = TestObjectBuilder.mapRentalToResponseDto(rental);
        when(rentalMapper.toDto(any(Rental.class))).thenReturn(expected);

        RentalRequestDto rentalRequestDto = new RentalRequestDto(1L, 2);
        RentalResponseDto actual = rentalService.rentCar(user, rentalRequestDto);

        assertThat(expected.getCarId()).isEqualTo(actual.getCarId());

        verify(carRepository, times(1))
                .findById(car.getId());
        verify(rentalRepository, times(1))
                .save(any(Rental.class));
        verify(carRepository, times(1))
                .save(car);
        verify(rentalNotificationService, times(1))
                .sendNotificationRentalCreated(any(), any(), any());
    }

    @Test
    @DisplayName("""
            Throw EntityNotFoundException when renting a car with an invalid car ID
            """)
    void rentCarWithInvalidCarId_throwsException() {
        RentalRequestDto rentalRequestDto = new RentalRequestDto(99L, 2);
        when(carRepository.findById(rentalRequestDto.carId())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () ->
                rentalService.rentCar(TestObjectBuilder.initUser(), rentalRequestDto));

        verify(carRepository, times(1))
                .findById(rentalRequestDto.carId());
    }

    @Test
    @DisplayName("""
            Throw EntityNotFoundException when renting a car with an invalid car ID
            """)
    void rentCarWithActiveRental_throwsException() {

        Rental rental = TestObjectBuilder.initRental();
        RentalRequestDto rentalRequestDto = new RentalRequestDto(rental.getCar().getId(), 2);
        when(rentalRepository.findByUserIdAndActualReturnDateIsNull(rental.getUser().getId()))
                .thenReturn(Optional.of(rental));
        assertThrows(ActiveRentalException.class, () ->
                rentalService.rentCar(rental.getUser(), rentalRequestDto));

        verify(rentalRepository, times(1))
                .findByUserIdAndActualReturnDateIsNull(rental.getUser().getId());
    }

    @Test
    @DisplayName("""
            Throw CarOutOfStockException when renting a car with inventory zero
            """)
    void rentCarWithInventoryZero_throwsException() {
        User user = TestObjectBuilder.initUser();
        when(rentalRepository.findByUserIdAndActualReturnDateIsNull(user.getId()))
                .thenReturn(Optional.empty());

        Car car = TestObjectBuilder.initCar();
        car.setInventory(0);

        RentalRequestDto rentalRequestDto = new RentalRequestDto(car.getId(), 3);
        when(carRepository.findById(car.getId())).thenReturn(Optional.of(car));
        assertThrows(CarOutOfStockException.class, () ->
                rentalService.rentCar(user, rentalRequestDto));

        verify(carRepository, times(1))
                .findById(car.getId());
    }

    @Test
    @DisplayName("""
            Successfully find active rental, returning a list with one element
            """)
    void findActiveRental_returnOneElement() {
        Rental rental = TestObjectBuilder.initRental();
        Long userId = rental.getUser().getId();
        when(rentalRepository.findByUserIdAndActualReturnDateIsNull(userId))
                .thenReturn(Optional.of(rental));

        RentalResponseDto expected = TestObjectBuilder.mapRentalToResponseDto(rental);
        when(rentalMapper.toDto(rental)).thenReturn(expected);

        List<RentalResponseDto> actual = rentalService
                .findActiveOrNoActiveRentals(TestObjectBuilder.initUser(), true);

        assertThat(actual).hasSize(1);
        assertThat(expected).isEqualTo(actual.get(0));

        verify(rentalRepository, times(1))
                .findByUserIdAndActualReturnDateIsNull(userId);
    }

    @Test
    @DisplayName("""
            Successfully find not active rentals, returning a list of rentals
            """)
    void findNotActiveRental_returnList() {
        Rental firstRental = TestObjectBuilder.initFirstCompletedRental();
        Long userId = firstRental.getUser().getId();
        Rental secondRental = TestObjectBuilder.initSecondCompletedRental();

        RentalResponseDto firstRentalDto = TestObjectBuilder.mapRentalToResponseDto(firstRental);
        RentalResponseDto secondRentalDto = TestObjectBuilder.mapRentalToResponseDto(secondRental);

        when(rentalRepository.findByUserIdAndActualReturnDateIsNotNull(userId))
                .thenReturn(List.of(firstRental, secondRental));
        when(rentalMapper.toDto(firstRental)).thenReturn(firstRentalDto);
        when(rentalMapper.toDto(secondRental)).thenReturn(secondRentalDto);

        List<RentalResponseDto> expected = List.of(firstRentalDto, secondRentalDto);
        User user = TestObjectBuilder.initUser();
        List<RentalResponseDto> actual = rentalService.findActiveOrNoActiveRentals(user, false);

        assertThat(actual).hasSize(2);
        assertThat(expected).isEqualTo(actual);

        verify(rentalRepository, times(1))
                .findByUserIdAndActualReturnDateIsNotNull(userId);
    }

    @Test
    @DisplayName("""
            Successfully find rental by ID with an existing rental
            """)
    void findRentalWithExistingId_success() {
        Rental rental = TestObjectBuilder.initRental();
        Long userId = rental.getUser().getId();
        Long rentalId = rental.getId();
        when(rentalRepository.findByIdAndUserId(rentalId, userId))
                .thenReturn(Optional.of(rental));

        RentalResponseDto expected = TestObjectBuilder.mapRentalToResponseDto(rental);
        when(rentalMapper.toDto(rental)).thenReturn(expected);

        User user = TestObjectBuilder.initUser();
        RentalResponseDto actual = rentalService.findRentalById(user, rentalId);

        assertThat(expected).isEqualTo(actual);
        verify(rentalRepository, times(1))
                .findByIdAndUserId(rentalId, userId);
    }

    @Test
    @DisplayName("""
            Throw EntityNotFoundException when finding rental by ID with a non-existing rental
            """)
    void findRentalWithNonExistingId_throwsException() {
        User user = TestObjectBuilder.initUser();
        when(rentalRepository.findByIdAndUserId(99L, user.getId())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () ->
                rentalService.findRentalById(user, 99L));

        verify(rentalRepository, times(1))
                .findByIdAndUserId(99L, user.getId());
    }

    @Test
    @DisplayName("""
            Successfully return a rented car with an active rental
            """)
    void returnCarWithActiveRental_success() {
        Rental rental = TestObjectBuilder.initRental();
        when(rentalRepository.findByUserIdAndActualReturnDateIsNull(anyLong()))
                .thenReturn(Optional.of(rental));

        Car car = TestObjectBuilder.initCar();
        when(carRepository.findById(anyLong())).thenReturn(Optional.of(car));

        car.setInventory(car.getInventory() + 1);
        rental.setActualReturnDate(LocalDateTime.now());

        User user = TestObjectBuilder.initUser();

        when(rentalRepository.save(any(Rental.class))).thenReturn(rental);
        when(carRepository.save(any(Car.class))).thenReturn(car);

        doNothing().when(rentalNotificationService)
                .sendNotificationRentalCompleted(rental, user, car);

        RentalResponseDto expected = TestObjectBuilder.mapRentalToResponseDto(rental);

        when(rentalMapper.toDto(rental)).thenReturn(expected);

        RentalResponseDto actual = rentalService.returnCar(user);

        assertThat(expected).isEqualTo(actual);

        verify(rentalRepository, times(1))
                .save(any(Rental.class));
        verify(carRepository, times(1))
                .save(any(Car.class));
        verify(rentalNotificationService, times(1))
                .sendNotificationRentalCompleted(any(), any(), any());
    }

    @Test
    @DisplayName("""
            Throw EntityNotFoundException when trying to return a car with no active rental
            """)
    void returnCarWithNoActiveRental_throwsException() {
        User user = TestObjectBuilder.initUser();
        when(rentalRepository.findByUserIdAndActualReturnDateIsNull(user.getId()))
                .thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () ->
                rentalService.returnCar(user));
        verify(rentalRepository, times(1))
                .findByUserIdAndActualReturnDateIsNull(anyLong());
    }
}
