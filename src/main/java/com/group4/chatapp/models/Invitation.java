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

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private RestrictionType restriction = RestrictionType.NONE;

    public boolean isFriendRequest() {
        return chatRoom == null;
    }

    public boolean isPending() {
        return status == Status.PENDING;
    }

    public boolean isAccepted() {
        return status == Status.ACCEPTED;
    }

    public boolean isBlock() {
        return restriction == RestrictionType.BLOCKED;
    }

    public boolean isMute() {
        return restriction == RestrictionType.MUTED;
    }

    public enum Status {
        PENDING, ACCEPTED, REJECTED
    }

    public enum RestrictionType {
        NONE, BLOCKED, MUTED
    }
}
