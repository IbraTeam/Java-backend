package com.IbraTeam.JavaBackend.Models.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.UUID;

@Data
public class KeyDTO {
    private UUID Id;

    @Pattern(regexp = "^[1-9][0-9]*((\\([0-9]+\\))[0-9]*)*$\n", message = "Номер аудитории может содержать только цифры и скобки")
    private String room;



}
