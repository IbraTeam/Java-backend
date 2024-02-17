package com.IbraTeam.JavaBackend.Services;

import com.IbraTeam.JavaBackend.Mappers.UserMapper;
import com.IbraTeam.JavaBackend.Models.Response;
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
            return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(),
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
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        }
        redisRepository.delete(tokenId);

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(), "Пользователь успешно вышел"), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> getUserProfile(User user){
        return ResponseEntity.ok(UserMapper.userToUserDto(user));
    }

    public ResponseEntity<?> getUsersWithChosenRoles(List<Role> roles, UsernameRequest name){
        List<UserDto> users;

        if (roles == null){
            users = userRepository.findAll()
                    .stream()
                    .map(UserMapper::userToUserDto)
                    .filter(user -> name == null || user.getName().contains(name.getName()))
                    .toList();
        }
        else {
            users = userRepository.findAllByRoleIn(roles)
                    .stream()
                    .map(UserMapper::userToUserDto)
                    .filter(user -> name == null || user.getName().contains(name.getName()))
                    .toList();

        }

        return ResponseEntity.ok(users);
    }

    @Transactional
    public ResponseEntity<?> giveRoleToUsers(User curUser, List<UUID> userIds, RoleRequest userRole){
        Role role = userRole.getRole();

        if (role == Role.DEAN && curUser.getRole() != Role.ADMIN){
            return new ResponseEntity<>(
                    new Response(HttpStatus.FORBIDDEN.value(),
                            "Вы не можете назначить пользователю роль деканат"), HttpStatus.FORBIDDEN);
        }
        else if (role == Role.ADMIN){
            return new ResponseEntity<>(
                    new Response(HttpStatus.FORBIDDEN.value(),
                            "Вы не можете назначить пользователю роль администратор"), HttpStatus.FORBIDDEN);
        }

        SimpleGrantedAuthority authority = getAuthority(role);
        for (UUID userId : userIds){
            User user = userRepository.findUserById(userId);
            if (user == null){
                return new ResponseEntity<>(
                        new Response(HttpStatus.NOT_FOUND.value(),
                                "Пользователь с id: " + userId + " не найден"), HttpStatus.NOT_FOUND);
            }

            Collection<GrantedAuthority> authorities = user.getAuthorities();

            if (!authorities.contains(authority)){
                user.setRole(role);
                authorities.add(authority);
            }

            userRepository.save(user);
        }

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(), "Роли успешно выданы"), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> deleteRoleFromUser(User curUser, UUID userId, RoleRequest userRole){
        Role role = userRole.getRole();

        if (role == Role.ADMIN){
            return new ResponseEntity<>(
                    new Response(HttpStatus.FORBIDDEN.value(),
                            "Вы не можете удалить пользователю данную роль"), HttpStatus.FORBIDDEN);
        }

        if (curUser.getRole() != Role.ADMIN && role == Role.DEAN){
            return new ResponseEntity<>(
                    new Response(HttpStatus.FORBIDDEN.value(),
                            "Вы не можете удалить пользователю данную роль"), HttpStatus.FORBIDDEN);
        }

        SimpleGrantedAuthority authority = getAuthority(role);
        User user = userRepository.findUserById(userId);
        if (user == null){
            return new ResponseEntity<>(
                    new Response(HttpStatus.NOT_FOUND.value(),
                            "Пользователь с id: " + userId + " не найден"), HttpStatus.NOT_FOUND);
        }

        if (!user.getAuthorities().contains(authority)){
            return new ResponseEntity<>(
                    new Response(HttpStatus.NOT_FOUND.value(),
                            "Пользователь с id: " + userId + " не содержит заданной роли"), HttpStatus.NOT_FOUND);
        }

        Collection<GrantedAuthority> authorities = user.getAuthorities();

        authorities.remove(authority);
        user.setRole(Role.USER);

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(), "Роли успешно удалены"), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> giveRoleDean(List<UUID> userIds){
        Role role = Role.DEAN;
        SimpleGrantedAuthority authority = getAuthority(role);

        for (UUID userId : userIds){
            User user = userRepository.findUserById(userId);
            if (user == null){
                return new ResponseEntity<>(
                        new Response(HttpStatus.NOT_FOUND.value(),
                                "Пользователь с id: " + userId + " не найден"), HttpStatus.NOT_FOUND);
            }

            if (!user.getAuthorities().contains(authority)){
                user.setRole(role);
                user.getAuthorities().add(authority);
            }
        }

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(), "Роли успешно выданы"), HttpStatus.OK);
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
}

