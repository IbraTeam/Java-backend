package com.IbraTeam.JavaBackend.Models.dto;

import com.IbraTeam.JavaBackend.Enums.KeyStatus;
import com.IbraTeam.JavaBackend.Models.Key.AudienceKey;
import lombok.Data;

import java.util.UUID;

@Data
public class KeyResponse {
    private UUID Id;

    private String userName;

    private String room;

    private KeyStatus transferStatus;
    public static KeyResponse from(AudienceKey key) {
        KeyResponse keyResponse = new KeyResponse();
        keyResponse.setId(key.getId());
        if (key.getUser() != null) {
            keyResponse.setUserName(key.getUser().getName());
        } else {
            keyResponse.setUserName(null);
        }
        keyResponse.setRoom(key.getRoom());
        keyResponse.setTransferStatus(key.getStatus());
        return keyResponse;
    }
}
