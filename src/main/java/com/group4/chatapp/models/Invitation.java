package com.group4.chatapp.models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.lang.Nullable;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User sender;

    @ManyToOne(optional = false)
    private User receiver;

    @Nullable
    @ManyToOne
    private ChatRoom chatRoom;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Status status;

    public boolean isFriendRequest() {
        return chatRoom == null;
    }

    public boolean isPending() {
        return status == Status.PENDING;
    }

    public enum Status {
        PENDING, ACCEPTED, REJECTED
    }
}
