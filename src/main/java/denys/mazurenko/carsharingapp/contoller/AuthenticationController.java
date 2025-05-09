package denys.mazurenko.carsharingapp.contoller;

import denys.mazurenko.carsharingapp.dto.user.JwtTokenResponseDto;
import denys.mazurenko.carsharingapp.dto.user.LoginUserRequestDto;
import denys.mazurenko.carsharingapp.dto.user.RegisterUserRequestDto;
import denys.mazurenko.carsharingapp.dto.user.UserResponseDto;
import denys.mazurenko.carsharingapp.exception.RegistrationException;
import denys.mazurenko.carsharingapp.security.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication controller",
        description = "Endpoints for managing users authentication")
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final AuthService authService;

    @Operation(summary = """
            Authenticates a user and generates a JWT token
            for accessing endpoints available to customers or managers.
            """)
    @PostMapping("/login")
    public JwtTokenResponseDto login(
            @RequestBody
            @Valid
            LoginUserRequestDto loginUserRequestDto) {
        return authService.authenticate(loginUserRequestDto);
    }

    @Operation(summary = """
            Registers a new user. All provided fields are mandatory and validated.
            If no user with the given email exists, registration will be successful.
            """)
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto register(
            @RequestBody
            @Valid
            RegisterUserRequestDto registerUserDto) throws RegistrationException {
        return authService.register(registerUserDto);
    }
}
