package denys.mazurenko.carsharingapp.mapper;

import denys.mazurenko.carsharingapp.config.MapperConfig;
import denys.mazurenko.carsharingapp.dto.user.RegisterUserRequestDto;
import denys.mazurenko.carsharingapp.dto.user.RegisterUserResponseDto;
import denys.mazurenko.carsharingapp.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    RegisterUserResponseDto toDto(User user);

    User toEntity(RegisterUserRequestDto registerUserRequestDto);
}
