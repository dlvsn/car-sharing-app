package denys.mazurenko.carsharingapp.service.rental;

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
import denys.mazurenko.carsharingapp.security.CustomUserDetailsService;
import denys.mazurenko.carsharingapp.service.notification.NotificationService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final int IS_OUT_OF_STOCK = 0;
    private final CustomUserDetailsService userDetailsService;
    private final NotificationService notificationService;
    private final CarRepository carRepository;
    private final RentalMapper rentalMapper;
    private final RentalRepository rentalRepository;

    @Transactional
    @Override
    public RentalResponseDto rentCar(
            Authentication authentication,
            RentalRequestDto rentalRequestDto
    ) {
        User user = userDetailsService.getUserFromAuthentication(authentication);
        checkActiveRental(user);

        Car car = findCarById(rentalRequestDto.carId());
        checkCarInventory(car);

        boolean isInventoryDecreased = false;
        car.setInventory(updateInventory(isInventoryDecreased, car.getInventory()));

        Rental rental = createRental(user, car, rentalRequestDto.days());

        carRepository.save(car);
        rentalRepository.save(rental);

        notificationService.sendNotificationRentCreated(rental, user, car);
        return rentalMapper.toDto(rental);
    }

    @Override
    public List<RentalResponseDto> findActiveOrNoActiveRentals(
            Authentication authentication,
            boolean isActive
    ) {
        User user = userDetailsService.getUserFromAuthentication(authentication);
        return isActive
                ? Stream.of(findActiveRentalByUserId(user.getId()))
                .map(rentalMapper::toDto)
                .toList()
                : rentalRepository.findByUserIdAndActualReturnDateIsNotNull(user.getId())
                .stream()
                .map(rentalMapper::toDto)
                .toList();
    }

    @Override
    public RentalResponseDto findRentalById(
            Authentication authentication,
            Long rentalId
    ) {
        User user = userDetailsService.getUserFromAuthentication(authentication);
        Rental rentalById = getRentalById(rentalId, user.getId());
        return rentalMapper.toDto(rentalById);
    }

    @Transactional
    @Override
    public RentalResponseDto returnCar(Authentication authentication) {
        User user = userDetailsService.getUserFromAuthentication(authentication);

        Rental rental = findActiveRentalByUserId(user.getId());
        rental.setActualReturnDate(LocalDateTime.now());

        boolean isInventoryDecreased = true;

        Car car = findCarById(rental.getCar().getId());
        car.setInventory(updateInventory(isInventoryDecreased, car.getInventory()));

        rentalRepository.save(rental);
        carRepository.save(car);
        notificationService.sendNotificationRentCompleted(rental, user, car);

        return rentalMapper.toDto(rental);
    }

    private Car findCarById(Long carId) {
        return carRepository.findById(carId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Can't find car by id " + carId)
                );
    }

    private Rental getRentalById(Long rentalId, Long userId) {
        return rentalRepository
                .findByIdAndUserId(rentalId, userId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Can't find rental by id " + rentalId)
                );
    }

    private void checkCarInventory(Car car) {
        if (car.getInventory() == IS_OUT_OF_STOCK) {
            throw new CarOutOfStockException("Car with id "
                    + car.getId()
                    + " is out of stock");
        }
    }

    private Rental findActiveRentalByUserId(Long userId) {
        return rentalRepository
                .findByUserIdAndActualReturnDateIsNull(userId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Can't find active rentals by userId "
                                + userId)
                );
    }

    private void checkActiveRental(User user) {
        Optional<Rental> rentalFromDb = rentalRepository
                .findByUserIdAndActualReturnDateIsNull(user.getId());
        if (rentalFromDb.isPresent()) {
            Rental rental = rentalFromDb.get();
            throw new ActiveRentalException(
                    String.format("""
                            You have an active rental.
                            Id: %d
                            Rental Date: %s
                            Car: %s %s
                            You can rent only one car!
                            """,
                            rental.getId(),
                            rental.getRentalDate(),
                            rental.getCar().getBrand(),
                            rental.getCar().getModel()
                    )
            );
        }
    }

    private Rental createRental(User user, Car car, int days) {
        Rental rental = new Rental();
        rental.setUser(user);
        rental.setCar(car);
        rental.setReturnDate(rental.getRentalDate().plusDays(days));
        return rental;
    }

    private int updateInventory(boolean value, int inventory) {
        return value ? inventory + 1 : inventory - 1;
    }
}
