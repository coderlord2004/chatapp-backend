package com.group4.chatapp.models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.lang.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Nullable
    @ManyToOne
    private File avatar;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public boolean equals(@Nullable Object obj) {

        if (obj instanceof User user) {
            return Objects.equals(this.id, user.id);
        } else {
            return false;
        }
    }

    @PreUpdate
    @PrePersist
    private void checkAvatarFileType() {
        if (avatar != null && !avatar.isImage()) {
            throw new IllegalStateException("Avatar must be an image");
        }
    }

    public boolean inChatRoom(ChatRoom room) {
        return room.getMembers().contains(this);
    }
}
