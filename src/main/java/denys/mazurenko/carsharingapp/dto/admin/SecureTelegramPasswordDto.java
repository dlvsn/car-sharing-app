package denys.mazurenko.carsharingapp.dto.admin;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record SecureTelegramPasswordDto(
        @NotBlank(message = "Please, enter password")
        @Length(min = 6, message = "Min length must be 6")
        String password) {
}
