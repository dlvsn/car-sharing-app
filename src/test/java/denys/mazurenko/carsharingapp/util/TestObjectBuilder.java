package denys.mazurenko.carsharingapp.util;

import com.stripe.model.checkout.Session;
import denys.mazurenko.carsharingapp.dto.payment.PaymentResponseDto;
import denys.mazurenko.carsharingapp.model.Car;
import denys.mazurenko.carsharingapp.model.Payment;
import denys.mazurenko.carsharingapp.model.Rental;
import denys.mazurenko.carsharingapp.model.User;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

public class TestObjectBuilder {

    public static User initUser() {
        User user = new User();
        user.setId(2L);
        user.setEmail("testmail@mail.com");
        user.setFirstName("testName");
        user.setLastName("testLastName");
        user.setPassword("1234567890");
        user.setDeleted(false);
        return user;
    }

    public static Car initCar() {
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

    public static Rental initRental() {
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

    public static Payment initPayment() {
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setRental(initRental());
        payment.setAmount(BigDecimal.valueOf(255.34));
        payment.setType(Payment.Type.PAYMENT);
        payment.setStatus(Payment.Status.PENDING);
        payment.setSessionUrl("test_url");
        payment.setSessionId("test_sessionId");
        return payment;
    }

    public static Payment initPaymentWithSessionFields(Session session) {
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setRental(initRental());
        payment.setAmount(BigDecimal.valueOf(session.getAmountTotal())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
        payment.setType(Payment.Type.PAYMENT);
        payment.setStatus(Payment.Status.PENDING);
        return payment;
    }

    public static PaymentResponseDto mapPaymentToResponseDto(Payment payment) {
        PaymentResponseDto paymentResponseDto = new PaymentResponseDto();
        paymentResponseDto.setRentalId(payment.getId());
        paymentResponseDto.setSessionId(payment.getSessionId());
        paymentResponseDto.setSessionUrl(payment.getSessionUrl());
        paymentResponseDto.setAmount(payment.getAmount());
        paymentResponseDto.setPaymentType(payment.getType());
        paymentResponseDto.setStatus(payment.getStatus());
        return paymentResponseDto;
    }

    public static PaymentResponseDto initPaymentResponseDto(Rental rental, Session session, Payment payment) {
        PaymentResponseDto paymentResponseDto = new PaymentResponseDto();
        paymentResponseDto.setRentalId(rental.getId());
        paymentResponseDto.setSessionId(session.getId());
        paymentResponseDto.setSessionUrl(session.getUrl());
        paymentResponseDto.setAmount(payment.getAmount());
        paymentResponseDto.setPaymentType(payment.getType());
        paymentResponseDto.setStatus(payment.getStatus());
        return paymentResponseDto;
    }
}
