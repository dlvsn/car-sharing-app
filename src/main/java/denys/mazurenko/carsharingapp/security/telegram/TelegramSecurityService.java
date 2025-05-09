package denys.mazurenko.carsharingapp.security.telegram;

import denys.mazurenko.carsharingapp.dto.admin.ChangeSecurePasswordDto;
import denys.mazurenko.carsharingapp.dto.admin.SecureTelegramPasswordDto;

public interface TelegramSecurityService {
    void createSecurePassword(SecureTelegramPasswordDto passwordDto);

    boolean isPasswordMatches(String password);

    void resetPassword(ChangeSecurePasswordDto passwordDto);
}
