package denys.mazurenko.carsharingapp.dto.user;

import lombok.Data;

@Data
public class RegisterUserResponseDto {
    private String email;
    private String firstName;
    private String lastName;
}
