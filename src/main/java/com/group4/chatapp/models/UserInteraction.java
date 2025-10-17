package com.group4.chatapp.models;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInteraction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User sourceUser;

    @ManyToOne(optional = false)
    private User targetUser;

    @Enumerated(EnumType.STRING)
    private InteractionType interactionType;

    private int count;

    @Column(name = "last_interaction")
    private Timestamp lastInteraction;

    public enum InteractionType {
        REACTION, COMMENT, MESSAGE, FOLLOW, POST
    }
}
