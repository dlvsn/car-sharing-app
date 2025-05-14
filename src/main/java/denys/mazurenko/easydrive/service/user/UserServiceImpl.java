package denys.mazurenko.easydrive.service.user;

import denys.mazurenko.easydrive.dto.user.UpdateProfileInfoRequestDto;
import denys.mazurenko.easydrive.dto.user.UpdateRolesRequestDto;
import denys.mazurenko.easydrive.dto.user.UserResponseDto;
import denys.mazurenko.easydrive.exception.EntityNotFoundException;
import denys.mazurenko.easydrive.mapper.UserMapper;
import denys.mazurenko.easydrive.model.Role;
import denys.mazurenko.easydrive.model.User;
import denys.mazurenko.easydrive.repository.user.RoleRepository;
import denys.mazurenko.easydrive.repository.user.UserRepository;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto updateRole(Long id, UpdateRolesRequestDto requestDto) {
        Set<Role> roleSet = roleRepository.findByIdIn(requestDto.rolesIds());
        if (roleSet.isEmpty()) {
            throw new EntityNotFoundException("Roles by ids "
                    + roleSet
                    + "not found");
        }
        User userFromDb = findUserById(id);
        userFromDb.setRoles(roleSet);
        userRepository.save(userFromDb);
        return userMapper.toDto(userFromDb);
    }

    @Override
    public UserResponseDto updateProfileInfo(
            User user,
            UpdateProfileInfoRequestDto requestDto
    ) {
        userMapper.updateUserFromDto(requestDto, user);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    public UserResponseDto getProfileInfo(User user) {
        return userMapper.toDto(findUserById(user.getId()));
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Can't find user by id " + id)
                );
    }
}
