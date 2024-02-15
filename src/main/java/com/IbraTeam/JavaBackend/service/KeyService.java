package com.IbraTeam.JavaBackend.service;

import com.IbraTeam.JavaBackend.Models.User.User;
import com.IbraTeam.JavaBackend.dto.KeyDTO;
import com.IbraTeam.JavaBackend.dto.KeyInfoDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface KeyService {

    List<KeyInfoDTO> getKeys(User user);
    KeyDTO createKey(KeyDTO keyDTO);

    void deleteKey(UUID keyId);

    void giveKey(UUID fromUserId, UUID userId, UUID keyId);

    void getKey(UUID keyId, LocalDateTime dateTime, User user);

    void acceptKey(UUID toUserId, UUID keyId);

    void rejectKey(UUID toUserId, UUID keyId);

    void cancelKeyTransfer(UUID fromUserId, UUID keyId);

    void returnKey(UUID keyId);


}
