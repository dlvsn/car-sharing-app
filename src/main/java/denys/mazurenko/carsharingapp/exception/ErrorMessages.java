package denys.mazurenko.carsharingapp.exception;

import lombok.Getter;

public class ErrorMessages {
    @Getter
    private static final String CAR = "car";
    @Getter
    private static final String USER = "user";

    @Getter
    private static final String CAR_EXIST_IN_DB = """
            Car\s
             \
            Brand: %s\s
             \
            Model: %s\s
             \
            Type: %s\s
             \
            is already exist in DB""";
    @Getter
    private static final String CANT_FIND_BY_ID = "Can't find %s by id: %d";

    @Getter
    private static final String USER_EXIST_IN_DB = "User with email %s already exist in DB";

    @Getter
    private static final String CANT_FIND_USER_BY_EMAIL = "Can't find user by email: %s";

    @Getter
    private static final String CAR_IS_OUT_OF_STOCK = "Car with id: %d is out of stock. "
            + "Please choose another one";

    @Getter
    private static final String NON_EXISTING_RENTAL = "You don't have an active rental!";

    @Getter
    private static final String EXISTING_RENTAL = """
            You have an active rental.
            Id: %d
            Rental Date: %s
            Car: %s %s
            You can rent only one car!
            """;
}
