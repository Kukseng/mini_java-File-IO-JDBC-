package model.service;

import model.dto.CreateUserDto;
import model.dto.UserRespondDto;

import java.util.List;

public interface UserService {
    UserRespondDto addNewUser(CreateUserDto createUserDto);

}
