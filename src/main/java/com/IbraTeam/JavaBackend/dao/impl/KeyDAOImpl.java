package com.IbraTeam.JavaBackend.dao.impl;

import com.IbraTeam.JavaBackend.Models.Key.AudienceKey;
import com.IbraTeam.JavaBackend.Models.Request.Request;
import com.IbraTeam.JavaBackend.Models.User.User;
import com.IbraTeam.JavaBackend.dao.KeyDAO;
import com.IbraTeam.JavaBackend.dao.repository.KeyRepository;
import com.IbraTeam.JavaBackend.dao.repository.RequestRepository;
import com.IbraTeam.JavaBackend.dto.KeyDTO;
import com.IbraTeam.JavaBackend.enums.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Component
public class KeyDAOImpl implements KeyDAO {

    @Autowired
    private KeyRepository keyRepository;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private RequestRepository requestRepository;
    
    @Override
    public List<KeyDTO> getKeys() {
        return null;

    }

    @Override
    public KeyDTO createKey(KeyDTO keyDTO) {
        AudienceKey audienceKey = new AudienceKey();
        audienceKey.setRoom(keyDTO.getRoom());
        audienceKey.setUser(null);
        keyDTO.setId(audienceKey.getId());
        audienceKey = keyRepository.save(audienceKey);

        return keyDTO;
    }

    @Override
    public void deleteKey(UUID keyId) {
        keyRepository.deleteById(keyId);
    }

    @Override
    public void giveKey(UUID userId, UUID keyId) {
        AudienceKey key = keyRepository.findById(keyId)
                .orElseThrow(() -> new IllegalArgumentException("Key not found with id: " + keyId));

        // Todo: прикрепить репозиторий юзера
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        key.setUser(user);
        keyRepository.save(key);
    }

    @Override
    public void getKey(UUID keyId, LocalDateTime dateTime, User user) {
        AudienceKey key = keyRepository.findById(keyId)
                .orElseThrow(() -> new IllegalArgumentException("Key not found with id: " + keyId));

        // Todo: понять, как присоединить это к реквестам дотнета
        Request request = requestRepository.findByAuthorAndKeyAndDateTimeAndStatus(user, key, dateTime, Status.Accepted)
                .orElseThrow(() -> new IllegalArgumentException("No accepted request found for user " +
                        user.getUsername() + " and keyId " + keyId + " at dateTime " + dateTime));

        key.setUser(user);
        keyRepository.save(key);

    }

    @Override
    public void returnKey(UUID keyId) {
        AudienceKey key = keyRepository.findById(keyId)
                .orElseThrow(() -> new IllegalArgumentException("Key not found with id: " + keyId));

        key.setUser(null);
        keyRepository.save(key);
    }
}
