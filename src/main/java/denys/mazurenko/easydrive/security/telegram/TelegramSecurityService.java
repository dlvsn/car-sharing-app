package denys.mazurenko.easydrive.security.telegram;

import denys.mazurenko.easydrive.dto.admin.ChangeSecurePasswordDto;
import denys.mazurenko.easydrive.dto.admin.SecureTelegramPasswordDto;

public interface TelegramSecurityService {
    void createSecurePassword(SecureTelegramPasswordDto passwordDto);

    boolean isPasswordMatches(String password);

    void resetPassword(ChangeSecurePasswordDto passwordDto);
}
