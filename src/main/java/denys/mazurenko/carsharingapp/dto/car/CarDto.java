package denys.mazurenko.carsharingapp.dto.car;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class CarDto {
    private String brand;
    private String model;
    private String type;
    private int inventory;
    private BigDecimal dailyFee;
}
