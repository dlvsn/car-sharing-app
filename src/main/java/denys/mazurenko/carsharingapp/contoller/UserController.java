package denys.mazurenko.carsharingapp.contoller;

import denys.mazurenko.carsharingapp.dto.user.UpdateProfileInfoRequestDto;
import denys.mazurenko.carsharingapp.dto.user.UpdateRolesRequestDto;
import denys.mazurenko.carsharingapp.dto.user.UserResponseDto;
import denys.mazurenko.carsharingapp.model.User;
import denys.mazurenko.carsharingapp.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public UserResponseDto getProfileInfo(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return userService.getProfileInfo(user);
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public UserResponseDto updateRoleByUserId(
            @PathVariable Long id,
            @RequestBody UpdateRolesRequestDto requestDto) {
        return userService.updateRole(id, requestDto);
    }

    @PutMapping("/me")
    public UserResponseDto updateProfileInfo(Authentication authentication,
                                             @RequestBody
                                             UpdateProfileInfoRequestDto requestDto) {
        User user = (User) authentication.getPrincipal();
        return userService.updateProfileInfo(user, requestDto);
    }
}
