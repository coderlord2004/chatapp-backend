package com.group4.chatapp.dtos.user;

import com.group4.chatapp.dtos.AttachmentDto;
import com.group4.chatapp.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserWithAvatarDto {

    private long id;
    private String username;

    @Nullable
    private String avatar;

    private Boolean isOnline;

    public UserWithAvatarDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.avatar = user.getAvatar();
        this.isOnline = user.getIsOnline();
    }
}
