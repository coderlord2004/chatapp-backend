package com.group4.chatapp.dtos.user;

import com.group4.chatapp.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.lang.Nullable;

import java.sql.Timestamp;

@Getter
@AllArgsConstructor
public class UserInformationDto {
    private long id;
    private String username;
    private String bio;
    private Timestamp createdAt;
    @Nullable
    private String avatar;

    @Nullable
    private String coverPicture;
    private Boolean isOnline;
    private Long totalFollowers;
    private Long totalFollowing;
    private Long totalPosts;

    public UserInformationDto(User user, Long totalFollowers, Long totalFollowing, Long totalPosts) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.bio = user.getBio();
        this.createdAt = user.getCreatedAt();
        this.avatar = user.getAvatar();
        this.coverPicture = user.getCoverPicture();
        this.isOnline = user.getIsOnline();
        this.totalFollowers = totalFollowers;
        this.totalFollowing = totalFollowing;
        this.totalPosts = totalPosts;
    }
}
