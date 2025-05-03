package com.group4.chatapp.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.lang.Nullable;

import java.sql.Timestamp;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Nullable
    private String name;

    @Nullable
    @ManyToOne
    private File avatar;

    @ManyToMany
    private Set<User> members;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Type type;

    @CreationTimestamp
    private Timestamp createdOn;

    private void checkMemberSize() {

        if (type == Type.DUO && members.size() != 2) {
            throw new IllegalStateException("DUO room must have exactly 2 members.");
        }

        if (type == Type.GROUP && members.size() < 3) {
            throw new IllegalStateException("GROUP room must have at least 3 members.");
        }
    }

    private void checkAvatarFileType() {
        if (avatar != null && !avatar.isImage()) {
            throw new IllegalStateException("Avatar must be an image.");
        }
    }

    @PreUpdate
    @PrePersist
    private void performChecks() {
        checkAvatarFileType();
        checkMemberSize();
    }

    public String getSocketPath() {
        return String.format("/queue/chat/%d", id);
    }

    public boolean isChatGroup() {
        return type == Type.GROUP;
    }

    public enum Type {
        DUO, GROUP
    }
}
