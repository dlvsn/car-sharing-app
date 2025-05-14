package denys.mazurenko.easydrive.service.user;

import denys.mazurenko.easydrive.dto.user.UpdateProfileInfoRequestDto;
import denys.mazurenko.easydrive.dto.user.UpdateRolesRequestDto;
import denys.mazurenko.easydrive.dto.user.UserResponseDto;
import denys.mazurenko.easydrive.model.User;

public interface UserService {
    UserResponseDto updateRole(Long id, UpdateRolesRequestDto requestDto);

    UserResponseDto updateProfileInfo(
            User user,
            UpdateProfileInfoRequestDto requestDto
    );

    UserResponseDto getProfileInfo(User user);
}
