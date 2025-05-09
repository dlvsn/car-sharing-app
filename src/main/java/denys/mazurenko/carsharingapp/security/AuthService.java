package denys.mazurenko.carsharingapp.security;

import denys.mazurenko.carsharingapp.dto.user.JwtTokenResponseDto;
import denys.mazurenko.carsharingapp.dto.user.LoginUserRequestDto;
import denys.mazurenko.carsharingapp.dto.user.RegisterUserRequestDto;
import denys.mazurenko.carsharingapp.dto.user.UserResponseDto;
import denys.mazurenko.carsharingapp.exception.RegistrationException;

public interface AuthService {
    JwtTokenResponseDto authenticate(LoginUserRequestDto loginUserRequestDto);

    UserResponseDto register(RegisterUserRequestDto registerUserDto) throws RegistrationException;
}
