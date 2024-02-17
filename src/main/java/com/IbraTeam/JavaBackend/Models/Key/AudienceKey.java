package com.IbraTeam.JavaBackend.Models.Key;


import com.IbraTeam.JavaBackend.Models.User.User;
import com.IbraTeam.JavaBackend.enums.KeyStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@Table(name = "keys")
@AllArgsConstructor
@NoArgsConstructor
public class AudienceKey {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String room;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KeyStatus status;
}
