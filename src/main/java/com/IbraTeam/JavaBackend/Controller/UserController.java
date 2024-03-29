package com.IbraTeam.JavaBackend.Controller;

import com.IbraTeam.JavaBackend.Models.Response.Response;
import com.IbraTeam.JavaBackend.Models.User.*;
import com.IbraTeam.JavaBackend.Services.IUserService;
import com.IbraTeam.JavaBackend.Enums.Role;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Transactional
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody @Valid UserRegisterModel userRegisterModel){
        userRegisterModel.setPassword(passwordEncoder.encode(userRegisterModel.getPassword()));

        try{
            return userService.registerNewUser(userRegisterModel);
        }
        catch (BadCredentialsException e) {
            return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(), "Данные введены некорректно"), HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginCredentials loginCredentials){
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginCredentials.getEmail(), loginCredentials.getPassword()));
        }
        catch (BadCredentialsException e) {
            return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(), "Неправильный логин или пароль"), HttpStatus.BAD_REQUEST);
        }catch (AuthenticationException e) {
            return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(), "Неправильный логин или пароль"), HttpStatus.BAD_REQUEST);
        }
        catch (Exception e){
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return userService.loginUser(loginCredentials);
    }

    @Transactional
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@RequestHeader("Authorization") String token){
        try {
            return userService.logoutUser(token);
        } catch (Exception e) {
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(@AuthenticationPrincipal User user){
        try {
            return userService.getUserProfile(user);
        } catch (Exception e) {
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/role")
    public ResponseEntity<?> getGreatestUserRole(@AuthenticationPrincipal User user){
        try {
            return ResponseEntity.ok(user.getRole());
        } catch (Exception e) {
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    @GetMapping("/users")
    public ResponseEntity<?> getUsers(@RequestParam(value = "roles", required = false)List<Role> roles,
                                      @RequestParam(name = "name", required = false) UsernameRequest name,
                                      @RequestParam(name = "page", defaultValue = "0") int page,
                                      @RequestParam(name = "size", defaultValue = "5") int size){
        try {
            return userService.getUsersWithChosenRoles(roles, name, page, size);
        }
        catch (Exception e) {
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    @PatchMapping("/role")
    public ResponseEntity<?> giveRoleToUsers(@AuthenticationPrincipal User user, @RequestParam(name = "userIds") List<UUID> userIds, @Valid @RequestBody RoleRequest role){
        try {
            return userService.giveRoleToUsers(user, userIds, role);
        }
        catch (HttpMessageNotReadableException e){
            return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(), "Заданной роли не существует"), HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    @DeleteMapping("/role/{userId}")
    public ResponseEntity<?> deleteRoleFromUser(@AuthenticationPrincipal User user, @PathVariable UUID userId, @RequestBody RoleRequest role){
        try {
            return userService.deleteRoleFromUser(user, userId, role);
        }
        catch (Exception e) {
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    @PatchMapping("/dean")
    public ResponseEntity<?> giveRoleDean(@RequestParam(name = "userIds") List<UUID> userIds){
        try {
            return userService.giveRoleDean(userIds);
        } catch (Exception e) {
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
