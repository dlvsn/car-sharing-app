package denys.mazurenko.carsharingapp.service.user;

import denys.mazurenko.carsharingapp.dto.user.RegisterUserRequestDto;
import denys.mazurenko.carsharingapp.dto.user.RegisterUserResponseDto;
import denys.mazurenko.carsharingapp.exception.RegistrationException;

public interface UserService {
    RegisterUserResponseDto register(RegisterUserRequestDto registerUserRequestDto) throws RegistrationException;

}
