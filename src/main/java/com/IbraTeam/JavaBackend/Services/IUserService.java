package com.IbraTeam.JavaBackend.Services;

import com.IbraTeam.JavaBackend.Models.User.*;
import com.IbraTeam.JavaBackend.Enums.Role;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface IUserService {
    ResponseEntity<?> registerNewUser(UserRegisterModel userRegisterModel);
    User loadUserByUsername(String email);
    ResponseEntity<?> loginUser(LoginCredentials loginCredentials);
    ResponseEntity<?> logoutUser(String token);
    ResponseEntity<?> getUserProfile(User user);
    ResponseEntity<?> getUsersWithChosenRoles(List<Role> roles, UsernameRequest name, int page, int size);
    ResponseEntity<?> giveRoleToUsers(User curUser, List<UUID> userIds, RoleRequest role);
    ResponseEntity<?> deleteRoleFromUser(User curUser, UUID userId, RoleRequest role);
    ResponseEntity<?> giveRoleDean(List<UUID> userIds);
}
