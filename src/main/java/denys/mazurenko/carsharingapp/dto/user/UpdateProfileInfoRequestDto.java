package denys.mazurenko.carsharingapp.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileInfoRequestDto {
    private String firstName;
    private String lastName;
}
