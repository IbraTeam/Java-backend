package com.IbraTeam.JavaBackend.dto;

import com.IbraTeam.JavaBackend.Models.Key.AudienceKey;
import lombok.Data;

import java.util.UUID;

@Data
public class KeyResponse {
    private UUID Id;

    private UUID userId;

    private String room;


    public static KeyResponse from(AudienceKey key) {
        KeyResponse keyResponse = new KeyResponse();
        keyResponse.setId(key.getId());
        if (key.getUser() != null) {
            keyResponse.setUserId(key.getUser().getId());
        } else {
            keyResponse.setUserId(null);
        }
        keyResponse.setRoom(key.getRoom());
        return keyResponse;
    }
}
