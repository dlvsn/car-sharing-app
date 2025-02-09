package denys.mazurenko.carsharingapp.service.rental;

import denys.mazurenko.carsharingapp.dto.rental.RentalRequestDto;
import denys.mazurenko.carsharingapp.dto.rental.RentalResponseDto;
import denys.mazurenko.carsharingapp.model.User;
import java.util.List;

public interface RentalService {
    RentalResponseDto rentCar(User user, RentalRequestDto requestDto);

    List<RentalResponseDto> findActiveOrNoActiveRentals(
            User user,
            boolean isActive
    );

    RentalResponseDto findRentalById(User user, Long rentalId);

    RentalResponseDto returnCar(User user);
}
