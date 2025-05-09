package denys.mazurenko.carsharingapp.repository;

import denys.mazurenko.carsharingapp.model.TelegramSecurePassword;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TelegramSecurePasswordRepository extends
        JpaRepository<TelegramSecurePassword, Long> {
    Optional<TelegramSecurePassword> findByOrderByIdDesc();
}
