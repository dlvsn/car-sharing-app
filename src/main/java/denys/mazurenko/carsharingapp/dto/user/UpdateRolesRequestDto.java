package denys.mazurenko.carsharingapp.dto.user;

import jakarta.validation.constraints.NotEmpty;
import java.util.Set;

public record UpdateRolesRequestDto(
        @NotEmpty
        Set<Long> rolesIds
) {
}
