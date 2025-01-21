package denys.mazurenko.carsharingapp.contoller;

import denys.mazurenko.carsharingapp.dto.admin.ChangeSecurePasswordDto;
import denys.mazurenko.carsharingapp.dto.admin.SecureTelegramPasswordDto;
import denys.mazurenko.carsharingapp.security.telegram.TelegramSecurityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin Controller",
        description = """
                Provides endpoints for managing 
                the admin's secure Telegram password. 
                Accessible only to users with the 'ROLE_MANAGER'.
                """)
@RequiredArgsConstructor
@RestController
@PreAuthorize("hasRole('ROLE_MANAGER')")
@RequestMapping("/admin")
public class AdminController {
    private final TelegramSecurityService telegramSecurityService;

    @Operation(summary = """
            Creates a secure Telegram password.
            This endpoint is used to set the password for the first time.
            Only one password can be created; to update it later, use the update endpoint.
            """)
    @PostMapping("/telegram")
    @ResponseStatus(HttpStatus.CREATED)
    public void setTelegramPassword(@RequestBody
                                        @Valid SecureTelegramPasswordDto passwordDto) {
        telegramSecurityService.createSecurePassword(passwordDto);
    }

    @Operation(summary = """
            Updates the existing secure Telegram password.
            Use this endpoint to change the password previously created.
            """)
    @PutMapping("/telegram")
    public void updateTelegramPassword(@RequestBody
                                           @Valid ChangeSecurePasswordDto passwordDto) {
        telegramSecurityService.resetPassword(passwordDto);
    }
}
