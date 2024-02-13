package com.IbraTeam.JavaBackend.Mappers;

import com.IbraTeam.JavaBackend.Models.User.User;
import com.IbraTeam.JavaBackend.Models.User.UserDto;
import com.IbraTeam.JavaBackend.Models.User.UserRegisterModel;
import com.IbraTeam.JavaBackend.enums.Role;

import java.util.UUID;

public class UserMapper {
    public static User userRegisterModelToUser(UserRegisterModel userRegisterModel){
        return new User(
                UUID.randomUUID(),
                userRegisterModel.getName(),
                userRegisterModel.getPassword(),
                userRegisterModel.getEmail(),
                Role.USER
        );
    }

    public static UserDto userToUserDto(User user){
        return new UserDto(
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }
}
