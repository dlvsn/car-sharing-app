package denys.mazurenko.carsharingapp.service.rental;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import denys.mazurenko.carsharingapp.dto.rental.RentalRequestDto;
import denys.mazurenko.carsharingapp.dto.rental.RentalResponseDto;
import denys.mazurenko.carsharingapp.exception.ActiveRentalException;
import denys.mazurenko.carsharingapp.exception.CarOutOfStockException;
import denys.mazurenko.carsharingapp.exception.EntityNotFoundException;
import denys.mazurenko.carsharingapp.mapper.RentalMapper;
import denys.mazurenko.carsharingapp.model.Car;
import denys.mazurenko.carsharingapp.model.Rental;
import denys.mazurenko.carsharingapp.model.User;
import denys.mazurenko.carsharingapp.repository.CarRepository;
import denys.mazurenko.carsharingapp.repository.RentalRepository;
import denys.mazurenko.carsharingapp.service.notification.rental.RentalNotificationService;
import denys.mazurenko.carsharingapp.util.TestObjectBuilder;
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

        car.setInventory(car.getInventory() - 1);
        Rental rental = TestObjectBuilder.initRental();

        when(carRepository.save(any(Car.class))).thenReturn(car);
        when(rentalRepository.save(any(Rental.class))).thenReturn(rental);
        doNothing().when(rentalNotificationService)
                .sendNotificationRentalCreated(any(), any(), any());

        RentalResponseDto expected = TestObjectBuilder.mapRentalToResponseDto(rental);
        when(rentalMapper.toDto(any(Rental.class))).thenReturn(expected);

        RentalRequestDto rentalRequestDto = new RentalRequestDto(1L, 2);
        RentalResponseDto actual = rentalService.rentCar(user, rentalRequestDto);

        assertThat(expected).isEqualTo(actual);

        verify(carRepository, times(1))
                .findById(car.getId());
        verify(rentalRepository, times(1))
                .save(any(Rental.class));
        verify(rentalNotificationService, times(1))
                .sendNotificationRentalCreated(any(), any(), any());
    }

    @Test
    @DisplayName("""
            Throw EntityNotFoundException when renting a car with an invalid car ID
            """)
    void rentCarWithInvalidCarId_throwsException() {
        RentalRequestDto rentalRequestDto = new RentalRequestDto(99L, 2);
        when(carRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () ->
                rentalService.rentCar(TestObjectBuilder.initUser(), rentalRequestDto));

        verify(carRepository, times(1))
                .findById(99L);
    }

    @Test
    @DisplayName("""
            Throw EntityNotFoundException when renting a car with an invalid car ID
            """)
    void rentCarWithActiveRental_throwsException() {
        RentalRequestDto rentalRequestDto = new RentalRequestDto(1L, 2);

        when(rentalRepository.findByUserIdAndActualReturnDateIsNull(anyLong()))
                .thenReturn(Optional.of(TestObjectBuilder.initRental()));
        assertThrows(ActiveRentalException.class, () ->
                rentalService.rentCar(TestObjectBuilder.initUser(), rentalRequestDto));

        verify(rentalRepository, times(1))
                .findByUserIdAndActualReturnDateIsNull(anyLong());
    }

    @Test
    @DisplayName("""
            Throw CarOutOfStockException when renting a car with inventory zero
            """)
    void rentCarWithInventoryZero_throwsException() {
        when(rentalRepository.findByUserIdAndActualReturnDateIsNull(anyLong()))
                .thenReturn(Optional.empty());

        Car car = TestObjectBuilder.initCar();
        car.setInventory(0);

        RentalRequestDto rentalRequestDto = new RentalRequestDto(1L, 3);
        when(carRepository.findById(anyLong())).thenReturn(Optional.of(car));
        assertThrows(CarOutOfStockException.class, () ->
                rentalService.rentCar(TestObjectBuilder.initUser(), rentalRequestDto));

        verify(carRepository, times(1))
                .findById(1L);
    }

    @Test
    @DisplayName("""
            Successfully find active rental, returning a list with one element
            """)
    void findActiveRental_returnOneElement() {
        Rental rental = TestObjectBuilder.initRental();
        when(rentalRepository.findByUserIdAndActualReturnDateIsNull(anyLong()))
                .thenReturn(Optional.of(rental));

        RentalResponseDto expected = TestObjectBuilder.mapRentalToResponseDto(rental);
        when(rentalMapper.toDto(rental)).thenReturn(expected);

        List<RentalResponseDto> actual = rentalService
                .findActiveOrNoActiveRentals(TestObjectBuilder.initUser(), true);

        assertThat(actual).hasSize(1);
        assertThat(expected).isEqualTo(actual.get(0));

        verify(rentalRepository, times(1))
                .findByUserIdAndActualReturnDateIsNull(anyLong());
    }

    @Test
    @DisplayName("""
            Successfully find not active rentals, returning a list of rentals
            """)
    void findNotActiveRental_returnList() {
        Rental firstRental = TestObjectBuilder.initFirstCompletedRental();
        Rental secondRental = TestObjectBuilder.initSecondCompletedRental();

        RentalResponseDto firstRentalDto = TestObjectBuilder.mapRentalToResponseDto(firstRental);
        RentalResponseDto secondRentalDto = TestObjectBuilder.mapRentalToResponseDto(secondRental);

        when(rentalRepository.findByUserIdAndActualReturnDateIsNotNull(anyLong()))
                .thenReturn(List.of(firstRental, secondRental));
        when(rentalMapper.toDto(firstRental)).thenReturn(firstRentalDto);
        when(rentalMapper.toDto(secondRental)).thenReturn(secondRentalDto);

        List<RentalResponseDto> expected = List.of(firstRentalDto, secondRentalDto);
        User user = TestObjectBuilder.initUser();
        List<RentalResponseDto> actual = rentalService.findActiveOrNoActiveRentals(user, false);

        assertThat(actual).hasSize(2);
        assertThat(expected).isEqualTo(actual);

        verify(rentalRepository, times(1))
                .findByUserIdAndActualReturnDateIsNotNull(anyLong());
    }

    @Test
    @DisplayName("""
            Successfully find rental by ID with an existing rental
            """)
    void findRentalWithExistingId_success() {
        Rental rental = TestObjectBuilder.initRental();
        when(rentalRepository.findByIdAndUserId(anyLong(), anyLong()))
                .thenReturn(Optional.of(rental));

        RentalResponseDto expected = TestObjectBuilder.mapRentalToResponseDto(rental);
        when(rentalMapper.toDto(rental)).thenReturn(expected);

        User user = TestObjectBuilder.initUser();
        RentalResponseDto actual = rentalService.findRentalById(user, rental.getId());

        assertThat(expected).isEqualTo(actual);
        verify(rentalRepository, times(1))
                .findByIdAndUserId(anyLong(), anyLong());
    }

    @Test
    @DisplayName("""
            Throw EntityNotFoundException when finding rental by ID with a non-existing rental
            """)
    void findRentalWithNonExistingId_throwsException() {
        when(rentalRepository.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () ->
                rentalService.findRentalById(TestObjectBuilder.initUser(), 99L));

        verify(rentalRepository, times(1))
                .findByIdAndUserId(anyLong(), anyLong());
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
        when(rentalRepository.findByUserIdAndActualReturnDateIsNull(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () ->
                rentalService.returnCar(TestObjectBuilder.initUser()));
        verify(rentalRepository, times(1))
                .findByUserIdAndActualReturnDateIsNull(anyLong());
    }
}
