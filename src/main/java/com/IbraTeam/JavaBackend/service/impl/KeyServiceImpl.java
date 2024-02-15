package com.IbraTeam.JavaBackend.service.impl;

import com.IbraTeam.JavaBackend.Models.Key.AudienceKey;
import com.IbraTeam.JavaBackend.Models.Key.KeyTransfer;
import com.IbraTeam.JavaBackend.Models.User.User;
import com.IbraTeam.JavaBackend.dao.KeyDAO;
import com.IbraTeam.JavaBackend.dto.KeyDTO;
import com.IbraTeam.JavaBackend.dto.KeyInfoDTO;
import com.IbraTeam.JavaBackend.service.KeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class KeyServiceImpl implements KeyService {

    @Autowired
    private KeyDAO keyDAO;

    @Override
    public List<KeyInfoDTO> getKeys(User user) {
        return keyDAO.getKeys(user);

    }

    @Override
    public KeyDTO createKey(KeyDTO keyDTO) {
        return keyDAO.createKey(keyDTO);
    }

    @Override
    public void deleteKey(UUID keyId) {
        keyDAO.deleteKey(keyId);
    }

    @Override
    public void giveKey(UUID fromUserId, UUID userId, UUID keyId) {
        keyDAO.giveKey(fromUserId, userId, keyId);
    }

    @Override
    public void getKey(UUID keyId, LocalDateTime dateTime, User user) {
        keyDAO.getKey(keyId, dateTime, user);
    }

    @Override
    public void acceptKey(UUID toUserId, UUID keyId) {
        keyDAO.acceptKey(toUserId, keyId);
    }

    @Override
    public void rejectKey(UUID toUserId, UUID keyId) {
        keyDAO.rejectKey(toUserId, keyId);
    }

    @Override
    public void cancelKeyTransfer(UUID fromUserId, UUID keyId) {
        keyDAO.cancelKeyTransfer(fromUserId, keyId);
    }

    @Override
    public void returnKey(UUID keyId) {
        keyDAO.returnKey(keyId);
    }
}
