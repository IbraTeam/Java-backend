package com.IbraTeam.JavaBackend.dto;

import com.IbraTeam.JavaBackend.enums.KeyStatus;
import com.IbraTeam.JavaBackend.enums.PairNumber;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class KeyInfoDTO {
    private UUID keyId;
    private String room;
    private LocalDateTime dateTime;

    private PairNumber pairNumber;

    private KeyStatus transferStatus;
    private String userName;

}
