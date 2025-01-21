package denys.mazurenko.carsharingapp.service.rental;

import denys.mazurenko.carsharingapp.dto.rental.RentalRequestDto;
import denys.mazurenko.carsharingapp.dto.rental.RentalResponseDto;
import java.util.List;
import org.springframework.security.core.Authentication;

public interface RentalService {
    RentalResponseDto rentCar(Authentication authentication, RentalRequestDto requestDto);

    List<RentalResponseDto> findActiveOrNoActiveRentals(
            Authentication authentication,
            boolean isActive
    );

    RentalResponseDto findRentalById(Authentication authentication, Long rentalId);

    RentalResponseDto returnCar(Authentication authentication);
}
