package denys.mazurenko.carsharingapp.mapper;

import denys.mazurenko.carsharingapp.config.MapperConfig;
import denys.mazurenko.carsharingapp.dto.user.RegisterUserRequestDto;
import denys.mazurenko.carsharingapp.dto.user.UpdateProfileInfoRequestDto;
import denys.mazurenko.carsharingapp.dto.user.UserResponseDto;
import denys.mazurenko.carsharingapp.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toDto(User user);

    User toEntity(RegisterUserRequestDto registerUserRequestDto);

    void updateUserFromDto(
            UpdateProfileInfoRequestDto requestDto,
            @MappingTarget User user
    );
}
