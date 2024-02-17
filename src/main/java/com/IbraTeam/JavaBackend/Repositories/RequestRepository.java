package com.IbraTeam.JavaBackend.Repositories;

import com.IbraTeam.JavaBackend.Models.Key.AudienceKey;
import com.IbraTeam.JavaBackend.Models.Request.Request;
import com.IbraTeam.JavaBackend.Models.User.User;
import com.IbraTeam.JavaBackend.Enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RequestRepository extends JpaRepository<Request, UUID> {
    Request findTopByAuthorAndKeyAndStatusOrderByDateTimeDesc(User user, AudienceKey key, Status issued);

    List<Request> findAllByAuthorAndKeyAndStatus(User user, AudienceKey key, Status accepted);
}
