package com.IbraTeam.JavaBackend.dao.repository;

import com.IbraTeam.JavaBackend.Models.Key.AudienceKey;
import com.IbraTeam.JavaBackend.Models.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface KeyRepository extends JpaRepository<AudienceKey, UUID> {

}
