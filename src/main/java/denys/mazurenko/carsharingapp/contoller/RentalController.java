package denys.mazurenko.carsharingapp.contoller;

import denys.mazurenko.carsharingapp.dto.rental.RentalRequestDto;
import denys.mazurenko.carsharingapp.dto.rental.RentalResponseDto;
import denys.mazurenko.carsharingapp.service.rental.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Rental controller", description = "Endpoints for managing rentals")
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/rentals")
public class RentalController {
    private final RentalService rentalService;

    @Operation(summary = """
            Creates a new car rental. 
            Each user is allowed to have only one active rental at a time.
            """)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RentalResponseDto createRent(
            Authentication authentication,
            @RequestBody
            @Valid
            RentalRequestDto rentalRequestDto) {
        return rentalService.rentCar(authentication, rentalRequestDto);
    }

    @Operation(summary = """
            Ends the current rental for the authenticated user.
            """)
    @PostMapping("/return")
    public RentalResponseDto returnCar(Authentication authentication) {
        return rentalService.returnCar(authentication);
    }

    @Operation(summary = """
            Retrieves rental details by its unique identifier for the authenticated user.
            """)
    @GetMapping("/{id}")
    public RentalResponseDto getRentalById(
            Authentication authentication,
            @PathVariable
            @Positive
            Long id) {
        return rentalService.findRentalById(authentication, id);
    }

    @Operation(summary = """
            Returns a list of rentals based on the isActive parameter. 
            Retrieves either active or inactive rentals for the authenticated user.
            """)
    @GetMapping
    public List<RentalResponseDto> getRentals(
            Authentication authentication,
            @RequestParam("is_active") boolean isActive) {
        return rentalService.findActiveOrNoActiveRentals(authentication, isActive);
    }
}
