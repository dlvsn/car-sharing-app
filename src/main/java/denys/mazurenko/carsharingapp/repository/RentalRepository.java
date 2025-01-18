package denys.mazurenko.carsharingapp.repository;

import denys.mazurenko.carsharingapp.model.Rental;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

    @EntityGraph(attributePaths = "car")
    Optional<Rental> findByUserIdAndActualReturnDateIsNull(Long userId);

    @EntityGraph(attributePaths = "car")
    Optional<Rental> findByIdAndUserIdAndActualReturnDateIsNotNull(Long rentalId, Long userId);
}
