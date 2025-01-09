package denys.mazurenko.carsharingapp.model;

import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

public class Rental {
    private Long id;
    @CreationTimestamp
    private LocalDate rentalDate;
    private LocalDate returnDate;
    private LocalDate actualReturnDate;
    private Car car;
    private User user;
}
