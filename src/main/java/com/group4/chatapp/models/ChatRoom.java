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

    @Column(nullable = false)
    private String name;

    @Column
    @Nullable
    private File avatar;

    @ManyToMany
    private Set<User> members;

    @Enumerated(EnumType.ORDINAL)
    private ChatRoomType type;

    @CreationTimestamp
    private Timestamp sentOn;

    @PreUpdate
    @PrePersist
    private void checkMemberSize() {

        if (type == ChatRoomType.DUO && members.size() != 2) {
            throw new IllegalStateException("DUO room must have exactly 2 members.");
        }

        if (type == ChatRoomType.GROUP && members.size() < 3) {
            throw new IllegalStateException("GROUP room must have at least 3 members.");
        }
    }

    @PreUpdate
    @PrePersist
    private void checkAvatarFileType() {

        if (avatar != null && !avatar.isImage()) {
            throw new IllegalStateException("Avatar must be an image");
        }
    }

    enum ChatRoomType {
        DUO,
        GROUP
    }
}
