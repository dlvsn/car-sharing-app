package denys.mazurenko.carsharingapp.repository.user;

import java.util.Optional;
import denys.mazurenko.carsharingapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByEmail(String email);

    boolean existsByEmail(String email);
}
