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
    private AttachmentDto avatar;

    public UserWithAvatarDto(User user) {

        this(
            user.getId(),
            user.getUsername(),
            null
        );

        var avatar = user.getAvatar();
        if (avatar != null) {
            this.avatar = new AttachmentDto(avatar);
        }
    }
}
