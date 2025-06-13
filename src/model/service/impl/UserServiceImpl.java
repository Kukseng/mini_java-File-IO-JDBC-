package model.service.impl;

import mapper.UserMapper;
import model.dto.CreateUserDto;
import model.dto.UserRespondDto;
import model.entities.User;
import model.repository.UserUserRepositoryImpl;
import model.service.UserService;

import java.util.List;

public class UserServiceImpl implements UserService {
    private static final UserUserRepositoryImpl USER_REPOSITORY_IMPL = new UserUserRepositoryImpl();

    @Override
    public UserRespondDto addNewUser(CreateUserDto createUserDto) {
        User user = UserMapper.fromCreateUserDtoToUser(createUserDto);
        return UserMapper.fromUserToUserResponseDto(USER_REPOSITORY_IMPL.save(user));
    }
}
