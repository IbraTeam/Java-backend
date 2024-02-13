package com.IbraTeam.JavaBackend.dto;

import java.util.UUID;

public class KeyDTO {
    private UUID Id;
    private String room;


    public void setId(UUID uuid) {
        this.Id = uuid;
    }

    public UUID getId() {
        return Id;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

}
