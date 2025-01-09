package denys.mazurenko.carsharingapp.service.user;

import java.util.HashSet;
import java.util.Set;
import denys.mazurenko.carsharingapp.dto.user.UpdateProfileInfoRequestDto;
import denys.mazurenko.carsharingapp.dto.user.UpdateRolesRequestDto;
import denys.mazurenko.carsharingapp.dto.user.UserResponseDto;
import denys.mazurenko.carsharingapp.exception.EntityNotFoundException;
import denys.mazurenko.carsharingapp.exception.ErrorMessages;
import denys.mazurenko.carsharingapp.mapper.UserMapper;
import denys.mazurenko.carsharingapp.model.Role;
import denys.mazurenko.carsharingapp.model.User;
import denys.mazurenko.carsharingapp.repository.user.RoleRepository;
import denys.mazurenko.carsharingapp.repository.user.UserRepository;
import denys.mazurenko.carsharingapp.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final CustomUserDetailsService customUserDetailsService;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    @Override
    public UserResponseDto updateRole(Long id, UpdateRolesRequestDto requestDto) {
        Set<Role> roleSet = roleRepository.findByIdIn(requestDto.rolesIds());
        User userFromDb = findUserById(id);
        userFromDb.setRoles(roleSet);
        userRepository.save(userFromDb);
        return userMapper.toDto(userFromDb);
    }

    @Override
    public UserResponseDto updateProfileInfo(User user, UpdateProfileInfoRequestDto requestDto) {
        User userFromDb = findUserById(user.getId());
        userMapper.updateUserFromDto(requestDto, userFromDb);
        userRepository.save(userFromDb);
        return userMapper.toDto(userFromDb);
    }

    @Override
    public UserResponseDto getProfileInfo(User user) {
        User userFromDb = findUserById(user.getId());
        return userMapper.toDto(userFromDb);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(String.format(
                                ErrorMessages.getCAR_EXIST_IN_DB(),
                                ErrorMessages.getCAR(),
                                id))
                );
    }
}
