package denys.mazurenko.carsharingapp.util;

import com.stripe.model.checkout.Session;
import denys.mazurenko.carsharingapp.dto.car.CarDto;
import denys.mazurenko.carsharingapp.dto.car.UpdateCarRequestDto;
import denys.mazurenko.carsharingapp.dto.payment.PaymentResponseDto;
import denys.mazurenko.carsharingapp.dto.rental.RentalResponseDto;
import denys.mazurenko.carsharingapp.dto.user.UpdateProfileInfoRequestDto;
import denys.mazurenko.carsharingapp.dto.user.UpdateRolesRequestDto;
import denys.mazurenko.carsharingapp.dto.user.UserResponseDto;
import denys.mazurenko.carsharingapp.model.Car;
import denys.mazurenko.carsharingapp.model.Payment;
import denys.mazurenko.carsharingapp.model.Rental;
import denys.mazurenko.carsharingapp.model.Role;
import denys.mazurenko.carsharingapp.model.User;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Set;

public class TestObjectBuilder {

    private TestObjectBuilder() {

    }

    public static CarDto initUpdatedCarDto(CarDto carDto, UpdateCarRequestDto requestDto) {
        carDto.setInventory(requestDto.inventory());
        carDto.setDailyFee(requestDto.dailyFee());
        return carDto;
    }

    public static CarDto initInvalidCarRequestDto() {
        CarDto carDto = new CarDto();
        carDto.setBrand("");
        carDto.setModel("");
        carDto.setType(Car.Type.SUV);
        carDto.setInventory(-5);
        carDto.setDailyFee(BigDecimal.valueOf(2));
        return carDto;
    }

    public static CarDto initFirstExistingCarDto() {
        CarDto carDto = new CarDto();
        carDto.setBrand("testBrand");
        carDto.setModel("testModel");
        carDto.setType(Car.Type.SUV);
        carDto.setInventory(50);
        carDto.setDailyFee(BigDecimal.valueOf(151.22));
        return carDto;
    }

    public static CarDto initSecondExistingCarDto() {
        CarDto carDto = new CarDto();
        carDto.setBrand("testBrand1");
        carDto.setModel("testModel1");
        carDto.setType(Car.Type.SUV);
        carDto.setInventory(0);
        carDto.setDailyFee(BigDecimal.valueOf(151.22));
        return carDto;
    }

    public static PaymentResponseDto initExpectedPaymentResponseDto() {
        PaymentResponseDto paymentResponseDto = new PaymentResponseDto();
        paymentResponseDto.setRentalId(1L);
        paymentResponseDto.setPaymentType(Payment.Type.PAYMENT);
        paymentResponseDto.setStatus(Payment.Status.PENDING);
        paymentResponseDto.setSessionUrl("test_url");
        paymentResponseDto.setSessionId("test_sessionId");
        paymentResponseDto.setAmount(BigDecimal.valueOf(143.34));
        return paymentResponseDto;
    }

    public static PaymentResponseDto initPendingPaymentResponseDto() {
        PaymentResponseDto paymentResponseDto = new PaymentResponseDto();
        paymentResponseDto.setStatus(Payment.Status.PENDING);
        paymentResponseDto.setPaymentType(Payment.Type.PAYMENT);
        paymentResponseDto.setRentalId(3L);
        paymentResponseDto.setAmount(BigDecimal.valueOf(151.22));
        return paymentResponseDto;
    }

    public static CarDto initCarDto() {
        CarDto carDto = new CarDto();
        carDto.setBrand("Toyota");
        carDto.setModel("Supra");
        carDto.setType(Car.Type.SPORT_CAR);
        carDto.setInventory(2);
        carDto.setDailyFee(BigDecimal.valueOf(1499.99));
        return carDto;
    }

    public static RentalResponseDto initRentalResponseDto() {
        RentalResponseDto rentalResponseDto = new RentalResponseDto();
        rentalResponseDto.setId(4L);
        rentalResponseDto.setCarId(1L);
        return rentalResponseDto;
    }

    public static RentalResponseDto initCompletedRentalResponseDto() {
        RentalResponseDto rentalResponseDto = new RentalResponseDto();
        rentalResponseDto.setId(1L);
        rentalResponseDto.setCarId(1L);
        return rentalResponseDto;
    }

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

    public static UpdateCarRequestDto initUpdateCarDto() {
        return new UpdateCarRequestDto(10, BigDecimal.valueOf(10));
    }

    public static CarDto mapCarToDto(Car car) {
        CarDto carDto = new CarDto();
        carDto.setBrand(car.getBrand());
        carDto.setModel(car.getModel());
        carDto.setType(car.getType());
        carDto.setInventory(car.getInventory());
        carDto.setDailyFee(car.getDailyFee());
        return carDto;
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

    public static Rental initFirstCompletedRental() {
        Rental rental = new Rental();
        rental.setId(2L);
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
        rental.setActualReturnDate(LocalDateTime.of(2024,
                12,
                28,
                14,
                30,
                00));
        rental.setUser(initUser());
        rental.setCar(initCar());
        return rental;
    }

    public static Rental initSecondCompletedRental() {
        Rental rental = new Rental();
        rental.setId(3L);
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
        rental.setActualReturnDate(LocalDateTime.of(2024,
                12,
                28,
                14,
                40,
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

    public static RentalResponseDto mapRentalToResponseDto(Rental rental) {
        RentalResponseDto response = new RentalResponseDto();
        response.setId(rental.getId());
        response.setRentalDate(rental.getRentalDate());
        response.setReturnDate(rental.getReturnDate());
        response.setCarId(rental.getCar().getId());
        response.setActualReturnDate(rental.getActualReturnDate());
        return response;
    }

    public static UserResponseDto initUserResponseDto() {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setEmail("testmail@mail.com");
        userResponseDto.setFirstName("testName");
        userResponseDto.setLastName("testLastName");
        return userResponseDto;
    }

    public static UpdateProfileInfoRequestDto initValidUpdateProfileDto() {
        UpdateProfileInfoRequestDto dto = new UpdateProfileInfoRequestDto();
        dto.setFirstName("Denys");
        dto.setLastName("Mazurenko");
        return dto;
    }

    public static UpdateProfileInfoRequestDto initInvalidUpdateProfileDto() {
        UpdateProfileInfoRequestDto dto = new UpdateProfileInfoRequestDto();
        dto.setFirstName("");
        dto.setLastName("");
        return dto;
    }

    public static UserResponseDto mapUserToDto(User user) {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setEmail(user.getEmail());
        userResponseDto.setFirstName(user.getFirstName());
        userResponseDto.setLastName(user.getLastName());
        return userResponseDto;
    }

    public static UpdateRolesRequestDto initUpdateRoleRequestDto() {
        return new UpdateRolesRequestDto(
                Set.of(1L, 2L));
    }

    public static UpdateProfileInfoRequestDto initUpdateProfileInfoDto() {
        UpdateProfileInfoRequestDto updateProfileInfoRequestDto = new UpdateProfileInfoRequestDto();
        updateProfileInfoRequestDto.setFirstName("test");
        updateProfileInfoRequestDto.setLastName("test");
        return updateProfileInfoRequestDto;
    }

    public static PaymentResponseDto initPaymentResponseDto(
            Rental rental, Session session, Payment payment) {
        PaymentResponseDto paymentResponseDto = new PaymentResponseDto();
        paymentResponseDto.setRentalId(rental.getId());
        paymentResponseDto.setSessionId(session.getId());
        paymentResponseDto.setSessionUrl(session.getUrl());
        paymentResponseDto.setAmount(payment.getAmount());
        paymentResponseDto.setPaymentType(payment.getType());
        paymentResponseDto.setStatus(payment.getStatus());
        return paymentResponseDto;
    }

    public static Role initUserRole() {
        Role role = new Role();
        role.setId(1L);
        role.setName(Role.Roles.ROLE_CUSTOMER);
        return role;
    }

    public static Role initManagerRole() {
        Role role = new Role();
        role.setId(2L);
        role.setName(Role.Roles.ROLE_MANAGER);
        return role;
    }

}
