package com.IbraTeam.JavaBackend.Repositories;

import com.IbraTeam.JavaBackend.Models.Key.AudienceKey;
import com.IbraTeam.JavaBackend.Models.Key.KeyTransfer;
import com.IbraTeam.JavaBackend.Models.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface KeyTransferRepository extends JpaRepository<KeyTransfer, UUID> {
    List<KeyTransfer> findAllByFromUserOrToUser(User fromUser, User toUser);

    Optional<KeyTransfer> findByKeyAndToUser(AudienceKey key, User toUser);

    Optional<KeyTransfer> findByKeyAndFromUser(AudienceKey key,User fromUser);
}
