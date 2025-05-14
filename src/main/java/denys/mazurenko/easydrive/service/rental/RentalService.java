package denys.mazurenko.easydrive.service.rental;

import denys.mazurenko.easydrive.dto.rental.RentalRequestDto;
import denys.mazurenko.easydrive.dto.rental.RentalResponseDto;
import denys.mazurenko.easydrive.model.User;
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
