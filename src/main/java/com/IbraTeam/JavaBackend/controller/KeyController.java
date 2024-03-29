package com.IbraTeam.JavaBackend.Controller;


import com.IbraTeam.JavaBackend.Exceptions.KeyAlreadyExistsException;
import com.IbraTeam.JavaBackend.Models.User.User;
import com.IbraTeam.JavaBackend.Models.dto.KeyDTO;
import com.IbraTeam.JavaBackend.Models.dto.KeyInfoDTO;
import com.IbraTeam.JavaBackend.Models.dto.KeyResponse;
import com.IbraTeam.JavaBackend.Exceptions.ResourceNotFoundException;
import com.IbraTeam.JavaBackend.Services.KeyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/audience-key")
public class KeyController {
    @Autowired
    private KeyService keyService;

    @GetMapping
    public ResponseEntity<List<KeyInfoDTO>> getKeys(@AuthenticationPrincipal User user) {
        List<KeyInfoDTO> keyInfoList = keyService.getKeys(user);
        return ResponseEntity.ok(keyInfoList);
    }

    @PostMapping
    public ResponseEntity<KeyDTO> createKey(@RequestBody KeyDTO keyDTO) throws KeyAlreadyExistsException {
        return ResponseEntity.ok(keyService.createKey(keyDTO));
    }

    @DeleteMapping("/{keyId}")
    public ResponseEntity deleteKey(@PathVariable UUID keyId) {
        keyService.deleteKey(keyId);
        return ResponseEntity.ok().build();
    }


    @PatchMapping("/give/{userId}")
    public ResponseEntity giveKey(
                                  @AuthenticationPrincipal User user,
                                  @PathVariable UUID userId,
                                  @RequestBody UUID keyId) {
        keyService.giveKey(user.getId(), userId, keyId);
        return ResponseEntity.ok().build();
    }


    @PatchMapping("/get/{keyId}")
    public ResponseEntity getKey(@PathVariable UUID keyId,
                                 @AuthenticationPrincipal User user) {
        keyService.getKey(keyId, user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/accept/{keyId}")
    public ResponseEntity acceptKey(@PathVariable UUID keyId,
                                    @AuthenticationPrincipal User user) {
        keyService.acceptKey(user.getId(), keyId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reject/{keyId}")
    public ResponseEntity rejectKey(@PathVariable UUID keyId,
                                 @AuthenticationPrincipal User user) {
        keyService.rejectKey(user.getId(), keyId);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/cancel/{keyId}")
    public ResponseEntity cancelKeyTransfer(@PathVariable UUID keyId,
                                            @AuthenticationPrincipal User user) {
        keyService.cancelKeyTransfer(user.getId(), keyId);
        return ResponseEntity.ok().build();
    }


    @PatchMapping("return/{keyId}")
    public ResponseEntity returnKey(@PathVariable UUID keyId) {
        keyService.returnKey(keyId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<KeyResponse>> getAllKeys() {
        return ResponseEntity.ok(keyService.getAllKeys());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        String errorMessage = ex.getMessage();
        return ResponseEntity.status(status).body(errorMessage);
    }

    @ExceptionHandler(KeyAlreadyExistsException.class)
    public ResponseEntity<Object> handleKeyAlreadyExistsException(KeyAlreadyExistsException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String errorMessage = ex.getMessage();
        return ResponseEntity.status(status).body(errorMessage);
    }

}

