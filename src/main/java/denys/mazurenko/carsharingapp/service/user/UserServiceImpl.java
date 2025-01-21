package denys.mazurenko.carsharingapp.service.user;

import denys.mazurenko.carsharingapp.dto.user.UpdateProfileInfoRequestDto;
import denys.mazurenko.carsharingapp.dto.user.UpdateRolesRequestDto;
import denys.mazurenko.carsharingapp.dto.user.UserResponseDto;
import denys.mazurenko.carsharingapp.exception.EntityNotFoundException;
import denys.mazurenko.carsharingapp.mapper.UserMapper;
import denys.mazurenko.carsharingapp.model.Role;
import denys.mazurenko.carsharingapp.model.User;
import denys.mazurenko.carsharingapp.repository.user.RoleRepository;
import denys.mazurenko.carsharingapp.repository.user.UserRepository;
import denys.mazurenko.carsharingapp.security.CustomUserDetailsService;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final CustomUserDetailsService userDetailsService;
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
    public UserResponseDto updateProfileInfo(
            Authentication authentication,
            UpdateProfileInfoRequestDto requestDto
    ) {
        User user = userDetailsService.getUserFromAuthentication(authentication);
        userMapper.updateUserFromDto(requestDto, user);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    public UserResponseDto getProfileInfo(Authentication authentication) {
        return userMapper.toDto(
                userDetailsService.getUserFromAuthentication(authentication)
        );
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Can't find user by id " + id)
                );
    }
}
