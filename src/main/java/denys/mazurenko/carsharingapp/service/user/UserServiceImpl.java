package denys.mazurenko.carsharingapp.service.user;

import denys.mazurenko.carsharingapp.dto.user.RegisterUserRequestDto;
import denys.mazurenko.carsharingapp.dto.user.RegisterUserResponseDto;
import denys.mazurenko.carsharingapp.exception.RegistrationException;
import denys.mazurenko.carsharingapp.mapper.UserMapper;
import denys.mazurenko.carsharingapp.model.User;
import denys.mazurenko.carsharingapp.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public RegisterUserResponseDto register(RegisterUserRequestDto registerUserRequestDto) throws RegistrationException {
        if (userRepository.existsByEmail(registerUserRequestDto.getEmail())) {
            throw new RegistrationException("User with email "
                    + registerUserRequestDto.getEmail()
                    + " already exists");
        }
        User newUser = userMapper.toEntity(registerUserRequestDto);
        userRepository.save(newUser);
        return userMapper.toDto(newUser);
    }
}
