package denys.mazurenko.easydrive.security;

import denys.mazurenko.easydrive.dto.user.JwtTokenResponseDto;
import denys.mazurenko.easydrive.dto.user.LoginUserRequestDto;
import denys.mazurenko.easydrive.dto.user.RegisterUserRequestDto;
import denys.mazurenko.easydrive.dto.user.UserResponseDto;
import denys.mazurenko.easydrive.exception.RegistrationException;

public interface AuthService {
    JwtTokenResponseDto authenticate(LoginUserRequestDto loginUserRequestDto);

    UserResponseDto register(RegisterUserRequestDto registerUserDto) throws RegistrationException;
}
