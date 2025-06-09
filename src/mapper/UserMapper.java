package mapper;

import model.dto.CreateUserDto;
import model.dto.UserRespondDto;
import model.entities.User;

import java.util.Random;
import java.util.UUID;

public class UserMapper {
    public static User fromCreateUserDtoToUser(CreateUserDto createUserDto){
        return User.builder()
                .id(new Random().nextInt(999999999))
                .uuid(UUID.randomUUID().toString())
                .username(createUserDto.username())
                .email(createUserDto.email())
                .password(createUserDto.password())
                .isDeleted(false)
                .build();
    }
    public static UserRespondDto fromUserToUserResponseDto(User user){
        return UserRespondDto
                .builder()
                .uuid(user.getUuid())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}
