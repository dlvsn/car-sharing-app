package denys.mazurenko.carsharingapp.contoller;

import denys.mazurenko.carsharingapp.dto.user.UpdateProfileInfoRequestDto;
import denys.mazurenko.carsharingapp.dto.user.UpdateRolesRequestDto;
import denys.mazurenko.carsharingapp.dto.user.UserResponseDto;
import denys.mazurenko.carsharingapp.model.User;
import denys.mazurenko.carsharingapp.security.CustomUserDetailsService;
import denys.mazurenko.carsharingapp.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "User controller", description = "Endpoints for managing users")
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/users")
public class UserController {
    private final CustomUserDetailsService userDetailsService;
    private final UserService userService;

    @Operation(summary = """
            Displays profile information for the currently authenticated user.
            """)
    @GetMapping("/me")
    public UserResponseDto getProfileInfo(Authentication authentication) {
        User user = userDetailsService.getUserFromAuthentication(authentication);
        return userService.getProfileInfo(user);
    }

    @Operation(summary = """
            Allows updating user roles. 
            Accessible only to users with the 'ROLE_MANAGER'.
            """)
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

    @Operation(summary = """
            Updates the profile information of the currently authenticated user.
            """)
    @PutMapping("/me")
    public UserResponseDto updateProfileInfo(Authentication authentication,
                                             @RequestBody
                                             @Valid
                                             UpdateProfileInfoRequestDto requestDto) {
        User user = userDetailsService.getUserFromAuthentication(authentication);
        return userService.updateProfileInfo(user, requestDto);
    }
}
