package denys.mazurenko.carsharingapp.dto.car;

import denys.mazurenko.carsharingapp.model.Car;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarDto {
    @NotBlank(message = "Please, add the brand car")
    private String brand;

    @NotBlank(message = "Please, add the model of car")
    private String model;

    @NotNull(message = "Please, add the type of car")
    private Car.Type type;

    @Positive(message = "Quantity less than 0")
    private int inventory;

    @NotNull
    private BigDecimal dailyFee;
}
