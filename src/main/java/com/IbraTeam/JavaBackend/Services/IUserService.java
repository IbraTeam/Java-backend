package com.IbraTeam.JavaBackend.Services;

import com.IbraTeam.JavaBackend.Models.User.LoginCredentials;
import com.IbraTeam.JavaBackend.Models.User.RoleRequest;
import com.IbraTeam.JavaBackend.Models.User.User;
import com.IbraTeam.JavaBackend.Models.User.UserRegisterModel;
import com.IbraTeam.JavaBackend.enums.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

public interface IUserService {
    ResponseEntity<?> registerNewUser(UserRegisterModel userRegisterModel);
    User loadUserByUsername(String email);
    ResponseEntity<?> loginUser(LoginCredentials loginCredentials);
    ResponseEntity<?> logoutUser(String token);
    ResponseEntity<?> getUserProfile(User user);
    ResponseEntity<?> getStudentsAndTeachers();
    ResponseEntity<?> giveRoleToUsers(User curUser, List<UUID> userIds, RoleRequest role);
    ResponseEntity<?> deleteRoleFromUser(User curUser, UUID userId, RoleRequest role);
    ResponseEntity<?> giveRoleDean(List<UUID> userIds);
}
