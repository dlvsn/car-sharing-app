package denys.mazurenko.easydrive.mapper;

import denys.mazurenko.easydrive.config.MapperConfig;
import denys.mazurenko.easydrive.dto.user.RegisterUserRequestDto;
import denys.mazurenko.easydrive.dto.user.UpdateProfileInfoRequestDto;
import denys.mazurenko.easydrive.dto.user.UserResponseDto;
import denys.mazurenko.easydrive.model.User;
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
