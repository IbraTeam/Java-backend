package com.IbraTeam.JavaBackend.dao;

import com.IbraTeam.JavaBackend.Models.Key.AudienceKey;
import com.IbraTeam.JavaBackend.Models.User.User;
import com.IbraTeam.JavaBackend.Models.dto.KeyDTO;
import com.IbraTeam.JavaBackend.Models.dto.KeyInfoDTO;

import java.util.List;
import java.util.UUID;

public interface KeyDAO {
    List<KeyInfoDTO> getKeys(User user);

    KeyDTO createKey(KeyDTO keyDTO);

    void deleteKey(UUID keyId);

    void giveKey(UUID fromUserId, UUID userId, UUID keyId);

    void getKey(UUID keyId, User user);

    void acceptKey(UUID toUserId, UUID keyId);

    void rejectKey(UUID toUserId, UUID keyId);

    void cancelKeyTransfer(UUID fromUserId, UUID keyId);

    void returnKey(UUID keyId);

    List<AudienceKey> getAllKeys();
}
