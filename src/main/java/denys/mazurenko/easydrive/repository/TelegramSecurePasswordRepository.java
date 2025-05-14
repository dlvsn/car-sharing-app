package denys.mazurenko.easydrive.repository;

import denys.mazurenko.easydrive.model.TelegramSecurePassword;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TelegramSecurePasswordRepository extends
        JpaRepository<TelegramSecurePassword, Long> {
    Optional<TelegramSecurePassword> findByOrderByIdDesc();
}
