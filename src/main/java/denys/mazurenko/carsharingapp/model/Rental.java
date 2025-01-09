package denys.mazurenko.carsharingapp.model;

import java.time.LocalDate;

public class Rental {
    private Long id;
    private LocalDate rentalDate;
    private LocalDate returnDate;
    private LocalDate actualReturnDate;
    private Car car;
    private User user;
}
