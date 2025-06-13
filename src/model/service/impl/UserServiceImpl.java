package model.service.impl;

import mapper.UserMapper;
import model.dto.CreateUserDto;
import model.dto.UserRespondDto;
import model.entities.User;
import model.repository.UserRepositoryImpl;
import model.service.UserService;
import model.service.PasswordUtil;

import java.util.List;

public class UserServiceImpl implements UserService {
    private static final UserRepositoryImpl USER_REPOSITORY_IMPL = new UserRepositoryImpl();

    @Override
    public UserRespondDto addNewUser(CreateUserDto createUserDto) {
        // Create user object without hashing password yet
        User user = UserMapper.fromCreateUserDtoToUser(createUserDto);

        // Make sure password is in plain text when passed to repository
        // The repository will handle the hashing
        return UserMapper.fromUserToUserResponseDto(USER_REPOSITORY_IMPL.save(user));
    }

    @Override
    public List<UserRespondDto> getAllUsers() {
        List<User> users = USER_REPOSITORY_IMPL.findAll();
        return users.stream()
                .map(UserMapper::fromUserToUserResponseDto)
                .toList();
    }

    @Override
    public UserRespondDto findUserByUuid(String uuid) {
        // You'll need to implement findByUuid in UserRepositoryImpl
        // For now, this is a placeholder
        return null;
    }

    @Override
    public Integer deleteUserByUuid(String uuid) {
        // You'll need to implement deleteByUuid in UserRepositoryImpl
        // For now, this is a placeholder
        return 0;
    }

    @Override
    public UserRespondDto updateUserByUuid(String uuid, CreateUserDto updateDto) {
        // You'll need to implement updateByUuid in UserRepositoryImpl
        // For now, this is a placeholder
        return null;
    }
}