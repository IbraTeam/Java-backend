package com.IbraTeam.JavaBackend.Models.Key;

import com.IbraTeam.JavaBackend.Models.User.User;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
public class KeyTransfer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "key_id")
    private AudienceKey key;

    @ManyToOne
    @JoinColumn(name = "from_user_id")
    private User fromUser;

    @ManyToOne
    @JoinColumn(name = "to_user_id")
    private User toUser;

    //@Enumerated(EnumType.STRING)
    //private Status status;
}
