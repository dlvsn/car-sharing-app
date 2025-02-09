package denys.mazurenko.carsharingapp.contoller;

import denys.mazurenko.carsharingapp.dto.car.CarDto;
import denys.mazurenko.carsharingapp.dto.car.UpdateCarRequestDto;
import denys.mazurenko.carsharingapp.service.car.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Car controller", description = "Endpoint for managing cars")
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/cars")
public class CarController {
    private final CarService carService;

    @Operation(summary = """
            Creates and saves a new car object in the database.
            All fields are validated.
            Accessible only to users with the 'ROLE_MANAGER'.
            """)
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CarDto create(
            @RequestBody
            @Valid CarDto carDto) {
        return carService.create(carDto);
    }

    @Operation(summary = """
            Retrieves a list of all cars available in the database.
            """)
    @GetMapping
    public List<CarDto> findAll() {
        return carService.getAllCars();
    }

    @Operation(summary = """
            Fetches a car from the database using its unique identifier.
            """)
    @GetMapping("/{id}")
    public CarDto findById(
            @PathVariable
            @Positive
            Long id) {
        return carService.getCarById(id);
    }

    @Operation(summary = """
            Updates the details of a car identified by its ID.
            """)
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PutMapping("/{id}")
    public CarDto updateCarById(
            @PathVariable
            @Positive
            Long id,
            @RequestBody
            @Valid
            UpdateCarRequestDto requestDto) {
        return carService.updateCar(id, requestDto);
    }

    @Operation(summary = """
            Deletes a car by its ID.
            Implements soft delete in the entity class. 
            Accessible only to users with the 'ROLE_MANAGER'.
            """)
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable
                               @Positive Long id) {
        carService.deleteCarById(id);
    }
}
