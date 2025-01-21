package denys.mazurenko.carsharingapp.contoller;

import denys.mazurenko.carsharingapp.dto.user.UpdateProfileInfoRequestDto;
import denys.mazurenko.carsharingapp.dto.user.UpdateRolesRequestDto;
import denys.mazurenko.carsharingapp.dto.user.UserResponseDto;
import denys.mazurenko.carsharingapp.service.user.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public UserResponseDto getProfileInfo(Authentication authentication) {
        return userService.getProfileInfo(authentication);
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public UserResponseDto updateRoleByUserId(
            @PathVariable
            @Positive
            Long id,
            @RequestBody
            @Valid
            UpdateRolesRequestDto requestDto) {
        return userService.updateRole(id, requestDto);
    }

    @PutMapping("/me")
    public UserResponseDto updateProfileInfo(Authentication authentication,
                                             @RequestBody
                                             @Valid
                                             UpdateProfileInfoRequestDto requestDto) {
        return userService.updateProfileInfo(authentication, requestDto);
    }
}
