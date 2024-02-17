package com.IbraTeam.JavaBackend.Repositories;

import com.IbraTeam.JavaBackend.Models.User.User;
import com.IbraTeam.JavaBackend.Enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    User findByEmail(String email);

    Page<User> findAllByRoleIn(List<Role> roles, Pageable pageable);
    int countByRoleIn(List<Role> roles);
    User findUserById(UUID id);
}
