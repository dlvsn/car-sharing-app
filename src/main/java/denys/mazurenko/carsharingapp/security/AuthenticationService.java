package denys.mazurenko.carsharingapp.security;

import denys.mazurenko.carsharingapp.dto.user.LoginUserResponseDto;
import denys.mazurenko.carsharingapp.dto.user.RegisterUserRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthenticationService {
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public LoginUserResponseDto authenticate(RegisterUserRequestDto userDto) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userDto.getEmail(), userDto.getPassword())
        );
        String generateToken = jwtUtil.generateToken(authentication.getName());
        return new LoginUserResponseDto(generateToken);
    }
}
