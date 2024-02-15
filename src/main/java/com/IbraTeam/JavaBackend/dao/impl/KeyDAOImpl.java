package com.IbraTeam.JavaBackend.dao.impl;

import com.IbraTeam.JavaBackend.Models.Key.AudienceKey;
import com.IbraTeam.JavaBackend.Models.Key.KeyTransfer;
import com.IbraTeam.JavaBackend.Models.Request.Request;
import com.IbraTeam.JavaBackend.Models.User.User;
import com.IbraTeam.JavaBackend.dao.KeyDAO;
import com.IbraTeam.JavaBackend.dao.repository.KeyRepository;
import com.IbraTeam.JavaBackend.dao.repository.KeyTransferRepository;
import com.IbraTeam.JavaBackend.dao.repository.RequestRepository;
import com.IbraTeam.JavaBackend.dto.KeyDTO;
import com.IbraTeam.JavaBackend.dto.KeyInfoDTO;
import com.IbraTeam.JavaBackend.enums.KeyStatus;
import com.IbraTeam.JavaBackend.enums.Status;
import com.IbraTeam.JavaBackend.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    
    @Autowired
    private KeyTransferRepository keyTransferRepository;


    @Override
    public List<KeyInfoDTO> getKeys(User user) {
        List<KeyInfoDTO> userKeys = new ArrayList<>();

        List<KeyTransfer> userTransfers = keyTransferRepository.findAllByFromUserOrToUser(user, user);


        for (KeyTransfer transfer : userTransfers) {
            AudienceKey key = transfer.getKey();
            KeyInfoDTO keyDTO = new KeyInfoDTO();
            keyDTO.setKeyId(key.getId());
            keyDTO.setRoom(key.getRoom());

            Request lastRequest = requestRepository.findTopByAuthorAndKeyAndStatusOrderByDateTimeDesc(
                    user, key, Status.Issued);

            if (lastRequest != null) {
                keyDTO.setDateTime(lastRequest.getDateTime());
                keyDTO.setPairNumber(lastRequest.getPairNumber());
            }

            // Мы отдаем ключик
            if (transfer.getFromUser().equals(user)) {
                keyDTO.setTransferStatus(KeyStatus.TRANSFERRING);
                keyDTO.setUserName(transfer.getToUser().getName());
            }

            // Нам отдают ключик
            if (transfer.getToUser().equals(user)) {
                keyDTO.setTransferStatus(KeyStatus.OFFERING_TO_YOU);
                keyDTO.setUserName(transfer.getFromUser().getName());
            }

            userKeys.add(keyDTO);
        }

        // Учли трансферные ключи, теперь нужно отследить на руках и ожидающие в деканате

        List<AudienceKey> keysOnHands = keyRepository.findByUser(user);
        for (AudienceKey key : keysOnHands) {
            Request lastRequest = requestRepository.findTopByAuthorAndKeyAndStatusOrderByDateTimeDesc(
                    user, key, Status.Issued);

            KeyInfoDTO keyDTO = new KeyInfoDTO();
            if (lastRequest != null) {
                keyDTO.setDateTime(lastRequest.getDateTime());
                keyDTO.setPairNumber(lastRequest.getPairNumber());
            }

            keyDTO.setKeyId(key.getId());
            keyDTO.setRoom(key.getRoom());
            keyDTO.setTransferStatus(KeyStatus.ON_HANDS);
            userKeys.add(keyDTO);
        }

        // Учли ключи на руках, теперь нужно выдать ключи, ожидающие в деканате

        List<AudienceKey> keysInDean = keyRepository.findAllByStatus(KeyStatus.IN_DEAN);
        for (AudienceKey key : keysInDean) {
            // Найдем для ключа последнюю заявку
            Request lastRequest = requestRepository.findTopByAuthorAndKeyOrderByDateTimeDesc(user, key);

            if (lastRequest != null && lastRequest.getStatus() == Status.Accepted) {
                // Если последняя заявка пользователя на этот ключ одобрена
                KeyInfoDTO keyDTO = new KeyInfoDTO();
                keyDTO.setKeyId(key.getId());
                keyDTO.setRoom(key.getRoom());
                keyDTO.setDateTime(lastRequest.getDateTime());
                keyDTO.setPairNumber(lastRequest.getPairNumber());
                keyDTO.setTransferStatus(KeyStatus.IN_DEAN); // Устанавливаем статус "В деканате"
                userKeys.add(keyDTO);
            }
        }


        // TODO: обработать адеватно повторяющиеся заявки и добавить в выдачу

        return userKeys;


    }

    @Override
    public KeyDTO createKey(KeyDTO keyDTO) {
        AudienceKey audienceKey = new AudienceKey();
        audienceKey.setRoom(keyDTO.getRoom());
        audienceKey.setUser(null);
        audienceKey.setStatus(KeyStatus.IN_DEAN);
        keyDTO.setId(audienceKey.getId());
        audienceKey = keyRepository.save(audienceKey);

        return keyDTO;
    }

    @Override
    public void deleteKey(UUID keyId) {
        keyRepository.deleteById(keyId);
    }

    @Override
    public void giveKey(UUID fromUserId, UUID userId, UUID keyId) {
        AudienceKey key = keyRepository.findById(keyId)
                .orElseThrow(() -> new ResourceNotFoundException("Key not found with id: " + keyId));

        // Todo: прикрепить репозиторий юзера
        User fromUser = userRepository.findById(fromUserId)
                .orElseThrow(() -> new ResourceNotFoundException("From user not found with id: " + fromUserId));

        User toUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("To user not found with id: " + userId));

        KeyTransfer keyTransfer = new KeyTransfer();
        keyTransfer.setKey(key);
        keyTransfer.setFromUser(fromUser);
        keyTransfer.setToUser(toUser);
        keyTransferRepository.save(keyTransfer);

        key.setUser(null); // Ключ сейчас в "трансферном состоянии"
        key.setStatus(KeyStatus.TRANSFERRING);
        keyRepository.save(key);
    }

    @Override
    public void getKey(UUID keyId, LocalDateTime dateTime, User user) {
        AudienceKey key = keyRepository.findById(keyId)
                .orElseThrow(() -> new ResourceNotFoundException("Key not found with id: " + keyId));

        // Todo: понять, как присоединить это к реквестам дотнета
        Request request = requestRepository.findByAuthorAndKeyAndDateTimeAndStatus(user, key, dateTime, Status.Accepted)
                .orElseThrow(() -> new ResourceNotFoundException("No accepted request found for user " +
                        user.getUsername() + " and keyId " + keyId + " at dateTime " + dateTime));

        key.setUser(user);
        key.setStatus(KeyStatus.ON_HANDS);
        request.setStatus(Status.Issued);
        keyRepository.save(key);

    }


    @Override
    public void acceptKey(UUID toUserId, UUID keyId) {
        User toUser = userRepository.findById(toUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + toUserId));

        AudienceKey key = keyRepository.findById(keyId)
                .orElseThrow(() -> new ResourceNotFoundException("Key not found with id: " + keyId));

        KeyTransfer keyTransfer = keyTransferRepository.findByKeyAndToUser(key, toUser)
                .orElseThrow(() -> new ResourceNotFoundException("Key transfer not found for key id: " + keyId));

        key.setUser(toUser);
        key.setStatus(KeyStatus.ON_HANDS);
        keyRepository.save(key);

        keyTransferRepository.delete(keyTransfer);
    }

    @Override
    public void rejectKey(UUID toUserId, UUID keyId) {
        User toUser = userRepository.findById(toUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + toUserId));

        AudienceKey key = keyRepository.findById(keyId)
                .orElseThrow(() -> new ResourceNotFoundException("Key not found with id: " + keyId));

        KeyTransfer keyTransfer = keyTransferRepository.findByKeyAndToUser(key, toUser)
                .orElseThrow(() -> new ResourceNotFoundException("Key transfer not found for key id: " + keyId));

        key.setUser(keyTransfer.getFromUser());
        key.setStatus(KeyStatus.ON_HANDS);
        keyRepository.save(key);

        keyTransferRepository.delete(keyTransfer);
    }

    @Override
    public void cancelKeyTransfer(UUID fromUserId, UUID keyId) {
        User fromUser = userRepository.findById(fromUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + fromUserId));

        AudienceKey key = keyRepository.findById(keyId)
                .orElseThrow(() -> new ResourceNotFoundException("Key not found with id: " + keyId));

        KeyTransfer keyTransfer = keyTransferRepository.findByKeyAndFromUser(key, fromUser)
                .orElseThrow(() -> new ResourceNotFoundException("Key transfer not found for key id: " + keyId));

        key.setUser(fromUser);
        key.setStatus(KeyStatus.ON_HANDS);
        keyRepository.save(key);

        keyTransferRepository.delete(keyTransfer);
    }

    @Override
    public void returnKey(UUID keyId) {
        AudienceKey key = keyRepository.findById(keyId)
                .orElseThrow(() -> new ResourceNotFoundException("Key not found with id: " + keyId));

        key.setUser(null);
        key.setStatus(KeyStatus.IN_DEAN);
        keyRepository.save(key);
    }
}
