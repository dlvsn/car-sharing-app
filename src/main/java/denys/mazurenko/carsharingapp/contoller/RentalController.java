package denys.mazurenko.carsharingapp.contoller;

import denys.mazurenko.carsharingapp.dto.rental.RentalRequestDto;
import denys.mazurenko.carsharingapp.dto.rental.RentalResponseDto;
import denys.mazurenko.carsharingapp.service.rental.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/rentals")
public class RentalController {
    private final RentalService rentalService;

    @PostMapping
    public RentalResponseDto createRent(
            Authentication authentication,
            @RequestBody
            RentalRequestDto rentalRequestDto) {
        return rentalService.rentCar(authentication, rentalRequestDto);
    }

    @PostMapping("/return")
    public RentalResponseDto returnCar(Authentication authentication) {
        return rentalService.returnCar(authentication);
    }
}
