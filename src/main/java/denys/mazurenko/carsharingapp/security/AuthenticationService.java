package denys.mazurenko.carsharingapp.security;

import denys.mazurenko.carsharingapp.dto.user.LoginUserRequestDto;
import denys.mazurenko.carsharingapp.dto.user.LoginUserResponseDto;
import denys.mazurenko.carsharingapp.dto.user.RegisterUserRequestDto;
import denys.mazurenko.carsharingapp.dto.user.UserResponseDto;
import denys.mazurenko.carsharingapp.exception.EntityNotFoundException;
import denys.mazurenko.carsharingapp.exception.ErrorMessages;
import denys.mazurenko.carsharingapp.exception.RegistrationException;
import denys.mazurenko.carsharingapp.mapper.UserMapper;
import denys.mazurenko.carsharingapp.model.Role;
import denys.mazurenko.carsharingapp.model.User;
import denys.mazurenko.carsharingapp.repository.user.RoleRepository;
import denys.mazurenko.carsharingapp.repository.user.UserRepository;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public LoginUserResponseDto authenticate(LoginUserRequestDto loginUserRequestDto) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginUserRequestDto.email(),
                        loginUserRequestDto.password()
                )
        );
        String generateToken = jwtUtil.generateToken(authentication.getName());
        return new LoginUserResponseDto(generateToken);
    }

    public UserResponseDto register(RegisterUserRequestDto registerUserDto)
            throws RegistrationException {
        if (userRepository.existsByEmail(registerUserDto.getEmail())) {
            throw new RegistrationException(String.format(
                    ErrorMessages.getUSER_EXIST_IN_DB(),
                    registerUserDto.getEmail()
            ));
        }
        User newUser = userMapper.toEntity(registerUserDto);
        Role roleCustomer = roleRepository
                .findByName(Role.Roles.ROLE_CUSTOMER)
                .orElseThrow(() ->
                        new EntityNotFoundException("Can't find role by name "
                                + Role.Roles.ROLE_CUSTOMER));
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        newUser.setRoles(new HashSet<>(Set.of(roleCustomer)));
        userRepository.save(newUser);
        return userMapper.toDto(newUser);
    }
}
