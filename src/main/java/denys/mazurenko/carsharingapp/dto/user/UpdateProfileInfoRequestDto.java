package denys.mazurenko.carsharingapp.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileInfoRequestDto {
    @NotBlank(message = "Please, enter first name")
    private String firstName;
    @NotBlank(message = "Please, enter last name")
    private String lastName;
}
