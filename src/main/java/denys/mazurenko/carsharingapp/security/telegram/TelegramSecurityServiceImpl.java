package denys.mazurenko.carsharingapp.security.telegram;

import denys.mazurenko.carsharingapp.dto.admin.ChangeSecurePasswordDto;
import denys.mazurenko.carsharingapp.dto.admin.SecureTelegramPasswordDto;
import denys.mazurenko.carsharingapp.exception.DataProcessingException;
import denys.mazurenko.carsharingapp.exception.EntityNotFoundException;
import denys.mazurenko.carsharingapp.model.TelegramSecurePassword;
import denys.mazurenko.carsharingapp.repository.TelegramSecurePasswordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TelegramSecurityServiceImpl implements TelegramSecurityService {
    private final TelegramSecurePasswordRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void createSecurePassword(SecureTelegramPasswordDto passwordDto) {
        if (repository.findByOrderByIdDesc().isPresent()) {
            throw new DataProcessingException(
                    "You can add only one password. "
                    + "Please, reset exist password"
            );
        }
        String encoded = passwordEncoder.encode(passwordDto.password());
        TelegramSecurePassword telegramSecurePassword = new TelegramSecurePassword();
        telegramSecurePassword.setPassword(encoded);
        repository.save(telegramSecurePassword);
    }

    @Override
    public boolean isPasswordMatches(String password) {
        TelegramSecurePassword telegramSecurePassword = getPassword();
        return passwordEncoder.matches(password, telegramSecurePassword.getPassword());
    }

    @Override
    public void resetPassword(ChangeSecurePasswordDto passwordDto) {
        TelegramSecurePassword telegramSecurePassword = getPassword();
        if (!passwordEncoder.matches(
                passwordDto.oldPassword(),
                telegramSecurePassword.getPassword())) {
            throw new DataProcessingException("Old passwords must match");
        }
        telegramSecurePassword.setPassword(passwordEncoder.encode(passwordDto.newPassword()));
        repository.save(telegramSecurePassword);
    }

    private TelegramSecurePassword getPassword() {
        return repository
                .findByOrderByIdDesc()
                .orElseThrow(() ->
                        new EntityNotFoundException("Can't find password"));
    }
}
