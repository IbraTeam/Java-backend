package com.IbraTeam.JavaBackend.dao;

import com.IbraTeam.JavaBackend.Models.User.User;
import com.IbraTeam.JavaBackend.dto.KeyDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface KeyDAO {
    List<KeyDTO> getKeys();

    KeyDTO createKey(KeyDTO keyDTO);

    void deleteKey(UUID keyId);

    void giveKey(UUID userId, UUID keyId);

    void getKey(UUID keyId, LocalDateTime dateTime, User user);

    void returnKey(UUID keyId);
}
