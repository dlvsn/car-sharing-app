package denys.mazurenko.carsharingapp.service.bot;

import java.util.Random;

public class Simple {
    private static final Random random = new Random();

    public int generateRandomNumber() {
        return 100000 + random.nextInt(900000);
    }
}
