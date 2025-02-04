package denys.mazurenko.carsharingapp.repository;

import denys.mazurenko.carsharingapp.model.Car;
import denys.mazurenko.carsharingapp.model.Payment;
import denys.mazurenko.carsharingapp.model.Rental;
import denys.mazurenko.carsharingapp.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {
        "classpath:database/test/user/insert-user.sql",
        "classpath:database/test/car/insert-car.sql",
        "classpath:database/test/rental/insert-rental.sql",
        "classpath:database/test/payment/insert-payment.sql"
}, executionPhase = BEFORE_TEST_METHOD)
@Sql(scripts = {
        "classpath:database/test/payment/delete-payment.sql",
        "classpath:database/test/rental/delete-rental.sql",
        "classpath:database/test/car/delete-car.sql",
        "classpath:database/test/user/delete-user.sql"
}, executionPhase = AFTER_TEST_METHOD)
public class PaymentRepositoryTest {
    private static final Long TEST_ID = 2L;
    private static final int EXPECTED_LIST_SIZE = 1;
    private static final int FIRST = 0;

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    @DisplayName("""
            """)
    void findByRentalUserIdFetchRental_Success() {
        Payment expectedPayment = initPayment();
        List<Payment> actual = paymentRepository.findByRentalUserIdFetchRental(TEST_ID);

        Payment payment = actual.get(FIRST);

        test(payment, expectedPayment);
        assertThat(actual).hasSize(EXPECTED_LIST_SIZE);
    }

    @Test
    @DisplayName("""
            """)
    void findByRentalIdFetchRental_Success() {
        Payment expectedPayment = initPayment();
        Payment actual = paymentRepository.findByRentalIdFetchRental(expectedPayment.getRental().getId(), TEST_ID).get();

        test(actual, expectedPayment);
    }

    private void test(Payment actual, Payment expectedPayment) {
        Long actualRentalId = actual.getRental().getId();
        Long actualCarId = actual.getRental().getCar().getId();
        Long actualUserId = actual.getRental().getUser().getId();
        String actualSessionUrl = actual.getSessionUrl();
        String actualUserEmail = actual.getRental().getUser().getEmail();

        assertThat(actualUserId).isEqualTo(TEST_ID);
        assertThat(actualUserEmail).isEqualTo(expectedPayment.getRental().getUser().getEmail());
        assertThat(actualRentalId).isEqualTo(expectedPayment.getId());
        assertThat(actualSessionUrl).isEqualTo(expectedPayment.getSessionUrl());
        assertThat(actualCarId).isEqualTo(expectedPayment.getRental().getCar().getId());
    }

    private Car initCar() {
        Car car = new Car();
        car.setId(1L);
        car.setBrand("testBrand");
        car.setModel("testModel");
        car.setType(Car.Type.SUV);
        car.setInventory(50);
        car.setDailyFee(BigDecimal.valueOf(151.22));
        car.setDeleted(false);
        return car;
    }

    private User initUser() {
        User user = new User();
        user.setId(2L);
        user.setEmail("testmail@mail.com");
        user.setFirstName("testName");
        user.setLastName("testLastName");
        user.setPassword("1234567890");
        user.setDeleted(false);
        return user;
    }

    private Rental initRental() {
        Rental rental = new Rental();
        rental.setId(1L);
        rental.setRentalDate(LocalDateTime.of(2024,
                12,
                28,
                14,
                30,
                00));
        rental.setReturnDate(LocalDateTime.of(2024,
                12,
                28,
                14,
                35,
                00));
        rental.setUser(initUser());
        rental.setCar(initCar());
        return rental;
    }

    private Payment initPayment() {
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setRental(initRental());
        payment.setAmount(BigDecimal.valueOf(143.34));
        payment.setType(Payment.Type.PAYMENT);
        payment.setStatus(Payment.Status.PENDING);
        payment.setSessionUrl("test_url");
        payment.setSessionId("test_sessionId");
        return payment;
    }
}