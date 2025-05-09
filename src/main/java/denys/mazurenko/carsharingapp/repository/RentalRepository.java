package denys.mazurenko.carsharingapp.repository;

import denys.mazurenko.carsharingapp.model.Rental;
import denys.mazurenko.carsharingapp.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {
    Optional<Rental> findByIdAndUserId(Long rentalId, Long userId);

    @EntityGraph(attributePaths = "car")
    Optional<Rental> findByUserIdAndActualReturnDateIsNull(Long userId);

    @EntityGraph(attributePaths = "car")
    Optional<Rental> findByIdAndUserIdAndActualReturnDateIsNotNull(Long rentalId, Long userId);

    List<Rental> findByUserIdAndActualReturnDateIsNotNull(Long userId);

    @EntityGraph(attributePaths = "user")
    List<Rental> findAllByActualReturnDateIsNull();

    Long user(User user);
}
