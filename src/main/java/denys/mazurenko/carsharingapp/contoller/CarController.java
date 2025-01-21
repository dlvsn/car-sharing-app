package denys.mazurenko.carsharingapp.contoller;

import denys.mazurenko.carsharingapp.dto.car.CarDto;
import denys.mazurenko.carsharingapp.dto.car.UpdateCarRequestDto;
import denys.mazurenko.carsharingapp.service.car.CarService;
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

@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/cars")
public class CarController {
    private final CarService carService;

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CarDto postNewCar(
            @RequestBody
            @Valid CarDto carDto) {
        return carService.save(carDto);
    }

    @GetMapping
    public List<CarDto> findAll() {
        return carService.getAllCars();
    }

    @GetMapping("/{id}")
    public CarDto findById(
            @PathVariable
            @Positive
            Long id) {
        return carService.getCarById(id);
    }

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

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable
                               @Positive Long id) {
        carService.deleteCarById(id);
    }
}
