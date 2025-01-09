package denys.mazurenko.carsharingapp.contoller;

import denys.mazurenko.carsharingapp.dto.user.LoginUserRequestDto;
import denys.mazurenko.carsharingapp.dto.user.LoginUserResponseDto;
import denys.mazurenko.carsharingapp.dto.user.RegisterUserRequestDto;
import denys.mazurenko.carsharingapp.dto.user.RegisterUserResponseDto;
import denys.mazurenko.carsharingapp.exception.RegistrationException;
import denys.mazurenko.carsharingapp.security.AuthenticationService;
import denys.mazurenko.carsharingapp.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final UserService userService;

    @PostMapping("/login")
    public LoginUserResponseDto login(@RequestBody LoginUserRequestDto loginUserRequestDto) {
        return authenticationService.authenticate(loginUserRequestDto);
    }

    @PostMapping("/register")
    public RegisterUserResponseDto register(@RequestBody RegisterUserRequestDto request) throws RegistrationException {
        return userService.register(request);
    }
}
