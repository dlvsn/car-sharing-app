package denys.mazurenko.carsharingapp.service.rental;

import denys.mazurenko.carsharingapp.dto.rental.RentalRequestDto;
import denys.mazurenko.carsharingapp.dto.rental.RentalResponseDto;
import org.springframework.security.core.Authentication;

public interface RentalService {
    RentalResponseDto rentCar(Authentication authentication, RentalRequestDto requestDto);

    RentalResponseDto returnCar(Authentication authentication);
}
