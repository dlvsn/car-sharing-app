package denys.mazurenko.easydrive.repository;

import denys.mazurenko.easydrive.model.Payment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("select p from Payment p "
            + "join fetch p.rental r "
            + "where r.user.id = :userId")
    List<Payment> findByRentalUserIdFetchRental(Long userId);

    @Query("select p from Payment p "
            + "join p.rental r "
            + "join r.user u "
            + "where r.id =:rentalId and u.id =:userId")
    Optional<Payment> findByRentalIdFetchRental(Long rentalId, Long userId);

    Optional<Payment> findBySessionId(String sessionId);
}
