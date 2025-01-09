package denys.mazurenko.carsharingapp.model;

import java.math.BigDecimal;
import java.net.URL;

public class Payment {
    private Long id;
    private Status status;
    private Type type;
    private Rental rental;
    private URL sessionUrl;
    private String sessionId;
    private BigDecimal amount;

    public enum Type {
        PAYMENT,
        FINE
    }

    public enum Status {
        PENDING,
        PAID
    }
}
