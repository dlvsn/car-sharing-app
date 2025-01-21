package denys.mazurenko.carsharingapp.dto.user;

import denys.mazurenko.carsharingapp.validator.user.FieldMatch;
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
    @Email
    @NotBlank(message = "Please, enter your email")
    private String email;

    @NotBlank(message = "Please, enter your first name")
    private String firstName;

    @NotBlank(message = "Please, enter your last name")
    private String lastName;

    @NotBlank
    @Length(min = 6, max = 20,
            message = "password must contain between 6 and 20 characters")
    private String password;
    private String repeatPassword;
}
