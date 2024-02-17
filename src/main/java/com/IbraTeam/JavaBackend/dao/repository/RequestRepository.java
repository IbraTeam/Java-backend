package com.IbraTeam.JavaBackend.dao.repository;

import com.IbraTeam.JavaBackend.Models.Key.AudienceKey;
import com.IbraTeam.JavaBackend.Models.Request.Request;
import com.IbraTeam.JavaBackend.Models.User.User;
import com.IbraTeam.JavaBackend.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RequestRepository extends JpaRepository<Request, UUID> {
    Optional<Request> findByAuthorAndKeyAndStatus(User author, AudienceKey key, Status status);

    Request findTopByAuthorAndKeyOrderByDateTimeDesc(User user, AudienceKey key);

    List<Request> findAllByAuthorAndRepeatedIsTrue(User user);

    Request findTopByAuthorAndKeyAndStatusOrderByDateTimeDesc(User user, AudienceKey key, Status issued);

    List<Request> findAllByAuthorAndKeyAndStatus(User user, AudienceKey key, Status accepted);
}
