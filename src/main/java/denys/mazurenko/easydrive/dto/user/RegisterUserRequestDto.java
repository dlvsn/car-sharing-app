package denys.mazurenko.easydrive.dto.user;

import denys.mazurenko.easydrive.validator.FieldMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@FieldMatch.List({
        @FieldMatch(
                first = "password",
                second = "repeatPassword",
                message = "password don't match")
})
public class RegisterUserRequestDto {
    @Email(message = "Invalid email format")
    @NotBlank(message = "Please, enter your email")
    private String email;

    @NotBlank(message = "Please, enter your first name")
    private String firstName;

    @NotBlank(message = "Please, enter your last name")
    private String lastName;

    @NotBlank
    @Length(min = 6, max = 20,
            message = "Password must contain between 6 and 20 characters")
    private String password;
    private String repeatPassword;
}
