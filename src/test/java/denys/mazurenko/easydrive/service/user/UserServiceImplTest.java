package denys.mazurenko.easydrive.service.user;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import denys.mazurenko.easydrive.dto.user.UpdateProfileInfoRequestDto;
import denys.mazurenko.easydrive.dto.user.UpdateRolesRequestDto;
import denys.mazurenko.easydrive.dto.user.UserResponseDto;
import denys.mazurenko.easydrive.exception.EntityNotFoundException;
import denys.mazurenko.easydrive.mapper.UserMapper;
import denys.mazurenko.easydrive.model.Role;
import denys.mazurenko.easydrive.model.User;
import denys.mazurenko.easydrive.repository.user.RoleRepository;
import denys.mazurenko.easydrive.repository.user.UserRepository;
import denys.mazurenko.easydrive.util.TestObjectBuilder;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserMapper userMapper;

    @Test
    @DisplayName("""
            Successfully update user roles with existing role IDs
            """)
    void updateRoleWithExistingIds_success() {
        Role roleUser = TestObjectBuilder.initUserRole();
        Role roleManager = TestObjectBuilder.initManagerRole();
        UpdateRolesRequestDto updateRolesRequestDto = TestObjectBuilder.initUpdateRoleRequestDto();

        when(roleRepository.findByIdIn(updateRolesRequestDto.rolesIds()))
                .thenReturn(Set.of(roleUser, roleManager));

        User user = TestObjectBuilder.initUser();
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        user.setRoles(Set.of(roleUser, roleManager));

        when(userRepository.save(user)).thenReturn(user);
        UserResponseDto expected = TestObjectBuilder.mapUserToDto(user);
        when(userMapper.toDto(user)).thenReturn(expected);

        UserResponseDto actual = userService.updateRole(user.getId(), updateRolesRequestDto);
        assertThat(expected).isEqualTo(actual);

        verify(userRepository, times(1))
                .findById(user.getId());
        verify(roleRepository, times(1))
                .findByIdIn(updateRolesRequestDto.rolesIds());
        verify(userRepository, times(1))
                .save(user);
        verify(userMapper, times(1))
                .toDto(user);
    }

    @Test
    @DisplayName("""
            Successfully update user profile information
            """)
    void updateProfileInfo_success() {
        UpdateProfileInfoRequestDto dto = TestObjectBuilder.initUpdateProfileInfoDto();
        User user = TestObjectBuilder.initUser();
        doNothing().when(userMapper).updateUserFromDto(dto, user);
        when(userRepository.save(user)).thenReturn(user);
        UserResponseDto expected = TestObjectBuilder.mapUserToDto(user);
        when(userMapper.toDto(user)).thenReturn(expected);
        UserResponseDto actual = userService.updateProfileInfo(user, dto);
        assertThat(expected).isEqualTo(actual);

        verify(userMapper, times(1))
                .updateUserFromDto(dto, user);
        verify(userRepository, times(1))
                .save(user);
        verify(userMapper, times(1))
                .toDto(user);
    }

    @Test
    @DisplayName("""
            Successfully retrieve user profile information with an existing user ID
            """)
    void getProfileInfoWithExistingId_success() {
        User user = TestObjectBuilder.initUser();
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        UserResponseDto expected = TestObjectBuilder.mapUserToDto(user);
        when(userMapper.toDto(user)).thenReturn(expected);

        UserResponseDto actual = userService.getProfileInfo(user);
        assertThat(expected).isEqualTo(actual);

        verify(userRepository, times(1))
                .findById(user.getId());
        verify(userMapper, times(1))
                .toDto(user);
    }

    @Test
    @DisplayName("""
            Throw EntityNotFoundException when 
            retrieving profile information with a non-existing user ID
            """)
    void getProfileInfoWithNoExistingId_throwsException() {
        User user = TestObjectBuilder.initUser();
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () ->
                userService.getProfileInfo(user));

        verify(userRepository, times(1))
                .findById(user.getId());
    }
}
