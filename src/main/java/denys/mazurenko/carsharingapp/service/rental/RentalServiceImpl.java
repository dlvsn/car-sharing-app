package denys.mazurenko.carsharingapp.service.rental;

import denys.mazurenko.carsharingapp.dto.rental.RentalRequestDto;
import denys.mazurenko.carsharingapp.dto.rental.RentalResponseDto;
import denys.mazurenko.carsharingapp.exception.ActiveRentalException;
import denys.mazurenko.carsharingapp.exception.CarOutOfStockException;
import denys.mazurenko.carsharingapp.exception.EntityNotFoundException;
import denys.mazurenko.carsharingapp.exception.ErrorMessages;
import denys.mazurenko.carsharingapp.mapper.RentalMapper;
import denys.mazurenko.carsharingapp.model.Car;
import denys.mazurenko.carsharingapp.model.Rental;
import denys.mazurenko.carsharingapp.model.User;
import denys.mazurenko.carsharingapp.repository.CarRepository;
import denys.mazurenko.carsharingapp.repository.RentalRepository;
import denys.mazurenko.carsharingapp.service.bot.NotificationService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
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
    private final NotificationService notificationService;
    private final CarRepository carRepository;
    private final RentalMapper rentalMapper;
    private final RentalRepository rentalRepository;

    @Transactional
    @Override
    public RentalResponseDto rentCar(
            Authentication authentication,
            RentalRequestDto rentalRequestDto) {
        User user = (User) authentication.getPrincipal();
        checkActiveRental(user);

        Car car = findCarById(rentalRequestDto.carId());
        checkCarInventory(car);

        boolean isInventoryDecreased = false;
        car.setInventory(updateInventory(isInventoryDecreased, car.getInventory()));

        Rental rental = createRental(user, car);

        carRepository.save(car);
        rentalRepository.save(rental);

        notificationService.sendNotificationRentCreated(rental, user, car);
        return rentalMapper.toDto(rental);
    }

    @Transactional
    @Override
    public RentalResponseDto returnCar(Authentication authentication) {
        User user = (User) authentication.getPrincipal();

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
                        new EntityNotFoundException(
                                String.format(
                                        ErrorMessages.getCANT_FIND_BY_ID(),
                                        ErrorMessages.getCAR(),
                                        carId
                                )
                        )
                );
    }

    private void checkCarInventory(Car car) {
        if (car.getInventory() == IS_OUT_OF_STOCK) {
            throw new CarOutOfStockException(
                    String.format(
                            ErrorMessages.getCAR_IS_OUT_OF_STOCK(),
                            car.getId()
                    )
            );
        }
    }

    private Rental findActiveRentalByUserId(Long userId) {
        return rentalRepository
                .findByUserIdAndActualReturnDateIsNull(userId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                ErrorMessages.getNON_EXISTING_RENTAL()
                        )
                );
    }

    private void checkActiveRental(User user) {
        Optional<Rental> rentalFromDb = rentalRepository
                .findByUserIdAndActualReturnDateIsNull(user.getId());
        if (rentalFromDb.isPresent()) {
            Rental rental = rentalFromDb.get();
            throw new ActiveRentalException(
                    String.format(
                            ErrorMessages.getEXISTING_RENTAL(),
                            rental.getId(),
                            rental.getRentalDate().format(DATE_TIME_FORMATTER),
                            rental.getCar().getBrand(),
                            rental.getCar().getModel()
                    )
            );
        }
    }

    private Rental createRental(User user, Car car) {
        Rental rental = new Rental();
        rental.setUser(user);
        rental.setCar(car);
        return rental;
    }

    private int updateInventory(boolean value, int inventory) {
        return value ? inventory + 1 : inventory - 1;
    }
}
