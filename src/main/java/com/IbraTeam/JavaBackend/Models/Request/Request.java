package com.IbraTeam.JavaBackend.Models.Request;

import com.IbraTeam.JavaBackend.Models.Key.AudienceKey;
import com.IbraTeam.JavaBackend.Models.User.User;
import com.IbraTeam.JavaBackend.enums.PairNumber;
import com.IbraTeam.JavaBackend.enums.Status;
import com.IbraTeam.JavaBackend.enums.Type;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "requests")
@AllArgsConstructor
@NoArgsConstructor
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(nullable = false)
    private LocalDateTime dateTime;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User author;

    @ManyToOne
    @JoinColumn(name = "key_id")
    private AudienceKey key;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private Type requestType;

    @Column(name = "pair_number", nullable = false)
    private PairNumber pairNumber;


    @Column(name = "repeat_count", nullable = false)
    private int repeatCount;

    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "repeat_id")
    private UUID repeatId;
}
