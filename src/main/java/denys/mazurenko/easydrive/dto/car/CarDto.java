package denys.mazurenko.easydrive.dto.car;

import denys.mazurenko.easydrive.model.Car;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarDto {
    @NotBlank(message = "Please, enter the brand car")
    private String brand;

    @NotBlank(message = "Please, enter the model of car")
    private String model;

    @NotNull(message = "Please, enter the type of car")
    private Car.Type type;

    @Positive(message = "Quantity can't be less than 0")
    private int inventory;

    @Positive(message = "Daily fee can't be less than 0")
    private BigDecimal dailyFee;
}
