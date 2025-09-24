package com.group4.chatapp.dtos.user;

import com.group4.chatapp.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.lang.Nullable;

@Getter
@AllArgsConstructor
public class UserInformationDto {
    private long id;
    private String username;
    private String bio;
    @Nullable
    private String avatar;

    @Nullable
    private String coverPicture;

    private Long totalFollowers;
    private Long totalFollowing;

    public UserInformationDto(User user, Long totalFollowers, Long totalFollowing) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.bio = user.getBio();
        this.avatar = user.getAvatar();
        this.coverPicture = user.getCoverPicture();
        this.totalFollowers = totalFollowers;
        this.totalFollowing = totalFollowing;
    }
}
