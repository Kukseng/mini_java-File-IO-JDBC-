package controller;

import model.dto.CreateUserDto;
import model.dto.UserRespondDto;
import model.service.UserService;
import model.service.impl.UserServiceImpl;

public class UserController {
    private static final UserService userService = new UserServiceImpl();
    public UserRespondDto createNewUser(CreateUserDto createUserDto){
        return userService.addNewUser(createUserDto);
    }
}
