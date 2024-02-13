package com.IbraTeam.JavaBackend.model;


import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "keys")
public class AudienceKey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String room;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private User user;


}
