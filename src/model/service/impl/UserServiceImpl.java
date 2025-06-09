package model.service.impl;

import mapper.UserMapper;
import model.dto.CreateUserDto;
import model.dto.UserRespondDto;
import model.entities.User;
import model.repository.UserRepository;
import model.service.UserService;

import java.util.List;

public class UserServiceImpl implements UserService {
    private static final UserRepository userRepository = new UserRepository();

    @Override
    public UserRespondDto addNewUser(CreateUserDto createUserDto) {
        User user = UserMapper.fromCreateUserDtoToUser(createUserDto);
        return UserMapper.fromUserToUserResponseDto(userRepository.save(user));
    }

    @Override
    public List<UserRespondDto> getAllUsers() {
        return List.of();
    }

    @Override
    public UserRespondDto findUserByUuid(String uuid) {
        return null;
    }

    @Override
    public Integer deleteUserByUuid(String uuid) {
        return 0;
    }

    @Override
    public UserRespondDto updateUserByUuid(String uuid, CreateUserDto updateDto) {
        return null;
    }
}
