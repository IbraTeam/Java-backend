package com.IbraTeam.JavaBackend.Repositories;

import com.IbraTeam.JavaBackend.Models.Key.AudienceKey;
import com.IbraTeam.JavaBackend.Models.User.User;
import com.IbraTeam.JavaBackend.Enums.KeyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface KeyRepository extends JpaRepository<AudienceKey, UUID> {
    List<AudienceKey> findByUser(User user);

    List<AudienceKey> findAllByStatus(KeyStatus status);

    boolean existsByRoom(String room);
}
