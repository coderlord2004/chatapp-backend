package com.group4.chatapp.dtos.user;

import com.group4.chatapp.dtos.AttachmentDto;
import com.group4.chatapp.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.lang.Nullable;

@Getter
@AllArgsConstructor
public class UserWithAvatarDto {

    private long id;
    private String username;

    @Nullable
    private String avatar;

    @Nullable
    private String coverPicture;

    public UserWithAvatarDto(User user) {

        this(
            user.getId(),
            user.getUsername(),
            user.getAvatar(),
            user.getCoverPicture()
        );

        var avatar = user.getAvatar();
        var coverPicture = user.getCoverPicture();
        if (avatar != null) {
            this.avatar = avatar;
        }
        if (coverPicture != null) {
            this.coverPicture = coverPicture;
        }
    }
}
