package denys.mazurenko.easydrive.dto.admin;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record ChangeSecurePasswordDto(
        @NotBlank(message = "Please, add value for old password")
        String oldPassword,
        @Length(min = 6, message = "Min size must be 6")
        String newPassword) {
}
