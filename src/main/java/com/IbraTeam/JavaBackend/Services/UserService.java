package com.IbraTeam.JavaBackend.Services;

import com.IbraTeam.JavaBackend.Exceptions.ExceptionResponse;
import com.IbraTeam.JavaBackend.Mappers.UserMapper;
import com.IbraTeam.JavaBackend.Models.User.*;
import com.IbraTeam.JavaBackend.Repositories.RedisRepository;
import com.IbraTeam.JavaBackend.Repositories.UserRepository;
import com.IbraTeam.JavaBackend.Utils.JwtTokenUtils;
import com.IbraTeam.JavaBackend.enums.Role;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService, IUserService {
    private final UserRepository userRepository;
    private final JwtTokenUtils jwtTokenUtils;
    private final RedisRepository redisRepository;

    @Override
    @Transactional
    public User loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public ResponseEntity<?> registerNewUser(UserRegisterModel userRegisterModel){
        if (userRepository.findByEmail(userRegisterModel.getEmail()) != null) {
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.BAD_REQUEST.value(),
                    "Пользователь с такой почтой уже существует"), HttpStatus.BAD_REQUEST);
        }

        User user = UserMapper.userRegisterModelToUser(userRegisterModel);

        userRepository.save(user);
        String token = jwtTokenUtils.generateToken(user);

        jwtTokenUtils.saveToken(jwtTokenUtils.getIdFromToken(token), "Valid");
        return ResponseEntity.ok(new JwtResponse(token));
    }

    @Transactional
    public ResponseEntity<?> loginUser(LoginCredentials loginCredentials){
        User user = userRepository.findByEmail(loginCredentials.getEmail());
        String token = jwtTokenUtils.generateToken(user);

        jwtTokenUtils.saveToken(jwtTokenUtils.getIdFromToken(token), "Valid");

        return ResponseEntity.ok(new JwtResponse(token));
    }

    @Transactional
    public ResponseEntity<?> logoutUser(String token){
        String tokenId = "";

        if (token != null) {
            token = token.substring(7);
            tokenId = jwtTokenUtils.getIdFromToken(token);
        }
        redisRepository.delete(tokenId);

        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<?> getUserProfile(User user){
        return ResponseEntity.ok(UserMapper.userToUserDto(user));
    }

    public ResponseEntity<?> getStudentsAndTeachers(){
        List<User> teachers = userRepository.findAllByRole(Role.TEACHER);
        List<User> students = userRepository.findAllByRole(Role.STUDENT);
        List<UserDto> users = new ArrayList<>();

        for (User teacher : teachers){
            users.add(UserMapper.userToUserDto(teacher));
        }

        for (User student : students){
            users.add(UserMapper.userToUserDto(student));
        }

        return ResponseEntity.ok(users);
    }

    @Transactional
    public ResponseEntity<?> giveRoleToUsers(List<UUID> userIds, String userRole){
        Role role;

        try {
            role = getRoleFromString(userRole);
        } catch (IllegalArgumentException e){
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.NOT_FOUND.value(), "Заданной роли не существует"), HttpStatus.NOT_FOUND);
        }

        if (role == Role.DEAN){
            return new ResponseEntity<>(
                    new ExceptionResponse(HttpStatus.FORBIDDEN.value(),
                            "Вы не можете назначить пользователю роль деканат"), HttpStatus.FORBIDDEN);
        }
        else if (role == Role.ADMIN){
            return new ResponseEntity<>(
                    new ExceptionResponse(HttpStatus.FORBIDDEN.value(),
                            "Вы не можете назначить пользователю роль администратор"), HttpStatus.FORBIDDEN);
        }

        SimpleGrantedAuthority authority = getAuthority(role);
        for (UUID userId : userIds){
            User user = userRepository.findUserById(userId);
            if (user == null){
                return new ResponseEntity<>(
                        new ExceptionResponse(HttpStatus.NOT_FOUND.value(),
                                "Пользователь с id: " + userId + " не найден"), HttpStatus.NOT_FOUND);
            }

            Collection<GrantedAuthority> authorities = user.getAuthorities();

            if (!authorities.contains(authority)){
                user.setRole(role);
                authorities.add(authority);
            }

            userRepository.save(user);
        }

        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<?> deleteRoleFromUser(User curUser, UUID userId, String userRole){
        Role role;

        try {
             role = getRoleFromString(userRole);
        } catch (IllegalArgumentException e){
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.NOT_FOUND.value(), "Заданной роли не существует"), HttpStatus.NOT_FOUND);
        }

        if (curUser.getRole() != Role.ADMIN && (role == Role.DEAN || role == Role.ADMIN )){
            return new ResponseEntity<>(
                    new ExceptionResponse(HttpStatus.FORBIDDEN.value(),
                            "Вы не можете удалить пользователю данную роль"), HttpStatus.FORBIDDEN);
        }

        SimpleGrantedAuthority authority = getAuthority(role);
        User user = userRepository.findUserById(userId);
        if (user == null){
            return new ResponseEntity<>(
                    new ExceptionResponse(HttpStatus.NOT_FOUND.value(),
                            "Пользователь с id: " + userId + " не найден"), HttpStatus.NOT_FOUND);
        }

        if (!user.getAuthorities().contains(authority)){
            return new ResponseEntity<>(
                    new ExceptionResponse(HttpStatus.NOT_FOUND.value(),
                            "Пользователь с id: " + userId + " не содержит заданной роли"), HttpStatus.NOT_FOUND);
        }

        Collection<GrantedAuthority> authorities = user.getAuthorities();

        authorities.remove(authority);
        user.setRole(Role.USER);

        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<?> giveRoleDean(List<UUID> userIds){
        Role role = Role.DEAN;
        SimpleGrantedAuthority authority = getAuthority(role);

        for (UUID userId : userIds){
            User user = userRepository.findUserById(userId);
            if (user == null){
                return new ResponseEntity<>(
                        new ExceptionResponse(HttpStatus.NOT_FOUND.value(),
                                "Пользователь с id: " + userId + " не найден"), HttpStatus.NOT_FOUND);
            }

            if (!user.getAuthorities().contains(authority)){
                user.setRole(role);
                user.getAuthorities().add(authority);
            }
        }

        return ResponseEntity.ok().build();
    }

    private SimpleGrantedAuthority getAuthority(Role role){
        return switch (role) {
            case DEAN -> new SimpleGrantedAuthority("ROLE_DEAN");
            case STUDENT -> new SimpleGrantedAuthority("ROLE_STUDENT");
            case TEACHER -> new SimpleGrantedAuthority("ROLE_TEACHER");
            case ADMIN -> new SimpleGrantedAuthority("ROLE_ADMIN");
            default -> new SimpleGrantedAuthority("ROLE_USER");
        };
    }

    private Role getRoleFromString(String role) {
        switch (role) {
            case "DEAN":
                return Role.DEAN;
            case "STUDENT":
                return Role.STUDENT;
            case "TEACHER":
                return Role.TEACHER;
            case "USER":
                return Role.USER;
            case "ADMIN":
                return Role.ADMIN;
            default:
                throw new IllegalArgumentException();
        }
    }

}

