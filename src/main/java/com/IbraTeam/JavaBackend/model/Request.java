package com.IbraTeam.JavaBackend.model;

import com.IbraTeam.JavaBackend.enums.PairNumber;
import com.IbraTeam.JavaBackend.enums.Status;
import com.IbraTeam.JavaBackend.enums.Type;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "requests")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(nullable = false)
    private LocalDateTime dateTime;

    @Column(nullable = false)
    private boolean repeated;

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

}
