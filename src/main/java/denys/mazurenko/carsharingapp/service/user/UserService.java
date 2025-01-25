package denys.mazurenko.carsharingapp.service.user;

import denys.mazurenko.carsharingapp.dto.user.UpdateProfileInfoRequestDto;
import denys.mazurenko.carsharingapp.dto.user.UpdateRolesRequestDto;
import denys.mazurenko.carsharingapp.dto.user.UserResponseDto;
import denys.mazurenko.carsharingapp.model.User;

public interface UserService {
    UserResponseDto updateRole(Long id, UpdateRolesRequestDto requestDto);

    UserResponseDto updateProfileInfo(
            User user,
            UpdateProfileInfoRequestDto requestDto
    );

    UserResponseDto getProfileInfo(User user);
}
