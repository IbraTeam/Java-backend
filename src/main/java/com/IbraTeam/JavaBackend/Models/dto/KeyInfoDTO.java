package com.IbraTeam.JavaBackend.Models.dto;

import com.IbraTeam.JavaBackend.Enums.KeyStatus;
import com.IbraTeam.JavaBackend.Enums.PairNumber;
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
