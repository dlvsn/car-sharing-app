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
}
