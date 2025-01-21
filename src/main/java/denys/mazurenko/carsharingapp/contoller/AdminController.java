package denys.mazurenko.carsharingapp.contoller;

import denys.mazurenko.carsharingapp.dto.admin.ChangeSecurePasswordDto;
import denys.mazurenko.carsharingapp.dto.admin.SecureTelegramPasswordDto;
import denys.mazurenko.carsharingapp.security.telegram.TelegramSecurityService;
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

@RequiredArgsConstructor
@RestController
@PreAuthorize("hasRole('ROLE_MANAGER')")
@RequestMapping("/admin")
public class AdminController {
    private final TelegramSecurityService telegramSecurityService;

    @PostMapping("/telegram")
    @ResponseStatus(HttpStatus.CREATED)
    public void setTelegramPassword(@RequestBody
                                        @Valid SecureTelegramPasswordDto passwordDto) {
        telegramSecurityService.createSecurePassword(passwordDto);
    }

    @PutMapping("/telegram")
    public void updateTelegramPassword(@RequestBody
                                           @Valid ChangeSecurePasswordDto passwordDto) {
        telegramSecurityService.resetPassword(passwordDto);
    }
}
