package com.IbraTeam.JavaBackend.controller;


import com.IbraTeam.JavaBackend.Models.User.User;
import com.IbraTeam.JavaBackend.dto.KeyDTO;
import com.IbraTeam.JavaBackend.service.KeyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("audience-key")
public class KeyController {
    @Autowired
    private KeyService keyService;

    @GetMapping
    public ResponseEntity<List<KeyDTO>> getKeys() {
        return ResponseEntity.ok(keyService.getKeys());
    }

    @PostMapping
    public ResponseEntity<KeyDTO> createKey(@RequestBody KeyDTO keyDTO) {
        return ResponseEntity.ok(keyService.createKey(keyDTO));
    }

    @DeleteMapping("/{keyId}")
    public ResponseEntity deleteKey(@PathVariable UUID keyId) {
        keyService.deleteKey(keyId);
        return ResponseEntity.ok().build();
    }


    @PatchMapping("/give/{userId}")
    public ResponseEntity giveKey(
                                  @PathVariable UUID userId,
                                  @RequestBody UUID keyId) {
        keyService.giveKey(userId, keyId);
        return ResponseEntity.ok().build();
    }


    @PatchMapping("/get/{keyId}")
    public ResponseEntity getKey(@PathVariable UUID keyId,
                                 @RequestBody LocalDateTime dateTime,
                                 @AuthenticationPrincipal User user) {
        keyService.getKey(keyId, dateTime, user);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("return/{keyId}")
    public ResponseEntity returnKey(@PathVariable UUID keyId) {
        keyService.returnKey(keyId);
        return ResponseEntity.ok().build();
    }
}

