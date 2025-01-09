package denys.mazurenko.carsharingapp.dto.user;

import lombok.Data;

@Data
public class RegisterUserRequestDto {
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String repeatPassword;
}
