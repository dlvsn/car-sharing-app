package denys.mazurenko.easydrive.dto.user;

import jakarta.validation.constraints.NotBlank;

public record LoginUserRequestDto(
        @NotBlank(message = "Please, enter your email")
        String email,
        @NotBlank(message = "Please, enter your password")
        String password) {
}
