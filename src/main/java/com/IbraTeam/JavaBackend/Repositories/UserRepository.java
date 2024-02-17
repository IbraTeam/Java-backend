package com.IbraTeam.JavaBackend.Repositories;

import com.IbraTeam.JavaBackend.Models.User.User;
import com.IbraTeam.JavaBackend.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    User findByEmail(String email);

    List<User> findAllByRoleIn(List<Role> roles);

    User findUserById(UUID id);
}
