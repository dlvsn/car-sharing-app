package denys.mazurenko.easydrive.dto.exception;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ErrorResponseDto {
    private final LocalDateTime timestamp;
    private final String exceptionName;
    private final HttpStatus status;
    private final List<String> exceptionMessage;

    public ErrorResponseDto(String exceptionName,
                            List<String> exceptionMessage,
                            HttpStatus status) {
        this.timestamp = LocalDateTime.now();
        this.exceptionName = exceptionName;
        this.exceptionMessage = exceptionMessage;
        this.status = status;
    }
}
