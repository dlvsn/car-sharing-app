package denys.mazurenko.carsharingapp.service.user;

import denys.mazurenko.carsharingapp.dto.user.UpdateChatIdRequestDto;
import denys.mazurenko.carsharingapp.dto.user.UpdateProfileInfoRequestDto;
import denys.mazurenko.carsharingapp.dto.user.UpdateRolesRequestDto;
import denys.mazurenko.carsharingapp.dto.user.UserResponseDto;
import org.springframework.security.core.Authentication;

public interface UserService {
    UserResponseDto updateUserTelegramChatId(Long userId, UpdateChatIdRequestDto chatIdRequestDto);

    UserResponseDto updateRole(Long id, UpdateRolesRequestDto requestDto);

    UserResponseDto updateProfileInfo(
            Authentication authentication,
            UpdateProfileInfoRequestDto requestDto
    );

    UserResponseDto getProfileInfo(Authentication authentication);
}
