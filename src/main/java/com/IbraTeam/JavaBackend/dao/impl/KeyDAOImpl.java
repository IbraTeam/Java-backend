package com.IbraTeam.JavaBackend.dao.impl;

import com.IbraTeam.JavaBackend.Models.Key.AudienceKey;
import com.IbraTeam.JavaBackend.Models.Key.KeyTransfer;
import com.IbraTeam.JavaBackend.Models.Request.Request;
import com.IbraTeam.JavaBackend.Models.User.User;
import com.IbraTeam.JavaBackend.Repositories.UserRepository;
import com.IbraTeam.JavaBackend.dao.KeyDAO;
import com.IbraTeam.JavaBackend.Repositories.KeyRepository;
import com.IbraTeam.JavaBackend.Repositories.KeyTransferRepository;
import com.IbraTeam.JavaBackend.Repositories.RequestRepository;
import com.IbraTeam.JavaBackend.Models.dto.KeyDTO;
import com.IbraTeam.JavaBackend.Models.dto.KeyInfoDTO;
import com.IbraTeam.JavaBackend.enums.KeyStatus;
import com.IbraTeam.JavaBackend.enums.Status;
import com.IbraTeam.JavaBackend.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
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

        // Учли трансферные ключи, теперь нужно отследить на руках

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

            List<Request> acceptedRequests = requestRepository.findAllByAuthorAndKeyAndStatus(user, key, Status.Accepted);


            Request firstRequest = acceptedRequests.stream()
                    .min(Comparator.comparing(Request::getDateTime))
                    .orElse(null);

            if (firstRequest != null) {
                // Если последняя заявка пользователя на этот ключ одобрена
                KeyInfoDTO keyDTO = new KeyInfoDTO();
                keyDTO.setKeyId(key.getId());
                keyDTO.setRoom(key.getRoom());
                keyDTO.setDateTime(firstRequest.getDateTime());
                keyDTO.setPairNumber(firstRequest.getPairNumber());
                keyDTO.setTransferStatus(KeyStatus.IN_DEAN);
                userKeys.add(keyDTO);
            }
        }



        return userKeys;


    }

    @Override
    public KeyDTO createKey(KeyDTO keyDTO) {
        AudienceKey audienceKey = new AudienceKey();
        audienceKey.setId(UUID.randomUUID());
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
    public void getKey(UUID keyId, User user) {
        AudienceKey key = keyRepository.findById(keyId)
                .orElseThrow(() -> new ResourceNotFoundException("Key not found with id: " + keyId));

        // Todo: понять, как присоединить это к реквестам дотнета
        List<Request> acceptedRequests = requestRepository.findAllByAuthorAndKeyAndStatus(user, key, Status.Accepted);

        Request firstRequest = acceptedRequests.stream()
                .min(Comparator.comparing(Request::getDateTime))
                .orElse(null);

        if (firstRequest == null) {
            throw new ResourceNotFoundException("На этот ключ от пользователя не было одобрено заявок");
        }

        key.setUser(user);
        key.setStatus(KeyStatus.ON_HANDS);
        firstRequest.setStatus(Status.Issued);
        keyRepository.save(key);
        requestRepository.save(firstRequest);
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

    @Override
    public List<AudienceKey> getAllKeys() {
        return keyRepository.findAll();
    }
}
